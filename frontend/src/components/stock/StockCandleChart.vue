<template>
  <div class="stock-chart-wrapper">
    <div class="chart-header">
      <h2 class="stock-symbol">{{ parsedSymbol }}</h2>
      <h3 class="current-price">${{ currentPrice.toFixed(2) }}</h3>
      <p class="price-change" :class="{ 'positive': priceChange >= 0, 'negative': priceChange < 0 }">
        {{ priceChangeFormatted }}
      </p>
    </div>
    <div class="chart-container" ref="chartContainer"></div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, watch, computed } from 'vue';
import { createChart, CandlestickSeries, HistogramSeries, PriceScaleMode } from 'lightweight-charts';
import { useStockStore } from '@/stores/stock';
import { getStockDetail } from '@/api/StockApi';
import { getCryptoHistory } from '@/api/CryptoApi';

// --- Props ---
const props = defineProps({
  symbol: {
    type: String,
    required: true,
  },
});

const parsedSymbol = ref('');

// --- Pinia Store ---
const stockStore = useStockStore();

// --- Chart-related reactive variables ---
const chartContainer = ref(null);
let chart = null;
let candleSeries = null;
let volumeSeries = null;
const OHLCV_data = {};
const INTERVAL_SECONDS = 5; // 5-second interval

// --- Price display ---
const openingPrice = ref(0); // Initialize with 0
const currentPrice = ref(0);

const priceChange = computed(() => {
  if (openingPrice.value === 0) return 0;
  return ((currentPrice.value - openingPrice.value) / openingPrice.value) * 100;
});

const priceChangeFormatted = computed(() => {
  const sign = priceChange.value >= 0 ? '+' : '';
  return `${sign}${priceChange.value.toFixed(2)}%`;
});

// --- Chart Data Handling ---
function processHistory(history) {
  history.forEach(trade => {
    const price = trade.price;
    const timeInSeconds = Math.floor(trade.timestamp / 1000);
    const candleTimeKey = timeInSeconds - (timeInSeconds % INTERVAL_SECONDS);

    let candle = OHLCV_data[candleTimeKey];
    if (!candle) {
      candle = {
        time: candleTimeKey,
        open: price,
        high: price,
        low: price,
        close: price,
        volume: 0, // No volume data in history, assume 0 for simplicity
      };
      OHLCV_data[candleTimeKey] = candle;
    } else {
      candle.high = Math.max(candle.high, price);
      candle.low = Math.min(candle.low, price);
      candle.close = price; // Update close with the latest price in the interval
    }
  });
}

// --- Chart Initialization ---
onMounted(async () => {
  // --- Symbol Parsing and Initial Data Fetch ---
  try {
    const fullSymbol = props.symbol;
    const parts = fullSymbol.split(':');

    if (parts.length > 1) {
      let symbolPart = parts[1];
      if (symbolPart.endsWith('USDT')) {
        parsedSymbol.value = symbolPart.slice(0, -4);
      } else if (symbolPart.endsWith('USDC')) {
        parsedSymbol.value = symbolPart.slice(0, -4);
      } else {
        parsedSymbol.value = symbolPart;
      }
    } else {
      parsedSymbol.value = fullSymbol;
    }

    const [detailResponse, historyResponse] = await Promise.all([
      getStockDetail(parsedSymbol.value),
      getCryptoHistory(parsedSymbol.value)
    ]);

    if (detailResponse.success) {
      openingPrice.value = detailResponse.data.o;
    }
     if (historyResponse.success) {
      processHistory(historyResponse.data);
    }

  } catch (error) {
    console.error('Failed to fetch initial chart data:', error);
  }

  // --- Chart Setup ---
  if (chartContainer.value) {
    chart = createChart(chartContainer.value, {
      layout: {
        background: { type: 'solid', color: '#131722' },
        textColor: '#d1d4dc',
      },
      grid: {
        vertLines: { color: '#2b2b43' },
        horzLines: { color: '#2b2b43' },
      },
      timeScale: {
        timeVisible: true,
        secondsVisible: true,
      },
      autoSize: true,
    });

    candleSeries = chart.addSeries(CandlestickSeries, {
      upColor: '#26a69a',
      downColor: '#ef5350',
      borderVisible: false,
      wickUpColor: '#26a69a',
      wickDownColor: '#ef5350',
      priceFormat: {
        type: 'price',
        precision: 6,
        minMove: 0.000001,
      },
    });

    volumeSeries = chart.addSeries(HistogramSeries, {
      color: '#26a69a',
      priceFormat: { type: 'volume' },
      priceScaleId: 'volume_scale',
      pane: 1,
    });

    chart.priceScale('volume_scale').applyOptions({
      mode: PriceScaleMode.Logarithmic,
      scaleMargins: {
        top: 0.9,
        bottom: 0,
      },
    });

    // --- Set Initial Data ---
    const allCandleData = Object.values(OHLCV_data).map(c => ({time: c.time, open: c.open, high: c.high, low: c.low, close: c.close})).sort((a,b) => a.time - b.time);
    const allVolumeData = Object.values(OHLCV_data).map(c => ({time: c.time, value: c.volume, color: c.open <= c.close ? '#26a69a' : '#ef5350'})).sort((a,b) => a.time - b.time);

    candleSeries.setData(allCandleData);
    volumeSeries.setData(allVolumeData);
    
    if (allCandleData.length > 0) {
        currentPrice.value = allCandleData[allCandleData.length-1].close
    }


    // --- Watch for real-time data from Pinia store ---
    watch(() => stockStore.latestTrades[props.symbol], (newTrade) => {
      if (newTrade && newTrade.p && newTrade.t && newTrade.v) {
        addTrade(newTrade);
      }
    }, {
      deep: true 
    });
  }
});

function addTrade(trade) {
  const price = trade.p;
  const volume = trade.v;
  const timeInSeconds = Math.floor(trade.t / 1000);
  const candleTimeKey = timeInSeconds - (timeInSeconds % INTERVAL_SECONDS);

  currentPrice.value = price;

  let candle = OHLCV_data[candleTimeKey];
  if (!candle) {
    // To get the last close price for the new candle's open
    const candleKeys = Object.keys(OHLCV_data).sort();
    const lastCandleKey = candleKeys[candleKeys.length - 1];
    const lastCandle = OHLCV_data[lastCandleKey];
    const openPrice = lastCandle ? lastCandle.close : price;

    candle = {
      time: candleTimeKey,
      open: openPrice,
      high: price,
      low: price,
      close: price,
      volume: volume,
    };
    OHLCV_data[candleTimeKey] = candle;
  } else {
    candle.high = Math.max(candle.high, price);
    candle.low = Math.min(candle.low, price);
    candle.close = price;
    candle.volume += volume;
  }
  
  const candleData = { time: candle.time, open: candle.open, high: candle.high, low: candle.low, close: candle.close };
  const volumeData = { time: candle.time, value: candle.volume, color: candle.open <= candle.close ? '#26a69a' : '#ef5350' };

  candleSeries.update(candleData);
  volumeSeries.update(volumeData);
}

// --- Component Cleanup ---
onUnmounted(() => {
  if (chart) {
    chart.remove();
    chart = null;
  }
});
</script>

<style scoped>
.stock-chart-wrapper {
  background: #131722;
  border-radius: 8px;
  padding: 20px;
  color: #d1d4dc;
}

.chart-header {
  text-align: left;
  margin-bottom: 15px;
}

.stock-symbol {
  font-size: 16px;
  color: #8a8a8a;
  font-weight: normal;
  margin: 0;
}

.current-price {
  font-size: 32px;
  font-weight: bold;
  color: #ffffff;
  margin: 5px 0;
}

.price-change {
  font-size: 18px;
  font-weight: 500;
  margin: 0;
}

.price-change.positive {
  color: #26a69a; /* Green */
}

.price-change.negative {
  color: #ef5350; /* Red */
}

.chart-container {
  width: 100%;
  height: 300px; /* Adjust height as needed */
}
</style>
