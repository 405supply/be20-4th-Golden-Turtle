<template>
  <div class="gt-box">
    <div class="stock-list-header">
      <input 
        type="text" 
        v-model="searchQuery" 
        placeholder="종목 검색" 
        class="stock-search-bar"
      />
    </div>
    <div class="stock-list-container">
      <table class="stock-table">
        <thead>
          <tr>
            <th>종목 코드</th>
            <th>종목 이름</th>
            <th>종목 시장</th>
            <th>상태</th>
            <th>현재가</th>
            <th>일일 변동</th>
          </tr>
        </thead>
        <tbody>
          <tr 
            v-for="(row, i) in paginatedRows" 
            :key="i" 
            :class="{'inactive-row': row.status === 'INACTIVE', 'clickable-row': row.status === 'ACTIVE'}"
            @click="row.status === 'ACTIVE' && goToStockDetail(row.fullSymbol)"
          >
            <td>{{ row.code }}</td>
            <td>{{ row.name }}</td>
            <td>{{ row.market }}</td>
            <td>{{ row.status }}</td>
            <td>{{ typeof row.price === 'number' ? '$' + row.price.toFixed(2) : row.price }}</td>
            <td :class="getChangeClass(row.change)">{{ row.change }}</td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- Pagination -->
    <Pagination
        v-model:currentPage="currentPage"
        :totalPages="totalPages"
    />
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from "vue";
import { useRouter } from 'vue-router';
import Pagination from '@/components/common/paging/Pagination.vue';
import { getAllStocks, getDailyPriceChanges } from '@/api/StockApi';
import { useStockStore } from '@/stores/stock';
import '@/assets/stock/StockList.css';

const router = useRouter();
const stockStore = useStockStore();

const tickerToKoreanName = {
  'BTC': '비트코인',
  'ETH': '이더리움',
  'XRP': '리플',
  'ADA': '에이다',
  'DOGE': '도지코인',
  'SOL': '솔라나',
  'LTC': '라이트코인',
  'BNB': '바이낸스코인',
  'DOT': '폴카닷',
  'LINK': '체인링크',
  'BCH': '비트코인캐시',
  'UNI': '유니스왑',
  'AVAX': '아발란체',
  'ICP': '인터넷컴퓨터',
  'VET': '비체인',
  'FIL': '파일코인',
  'TRX': '트론',
  'XLM': '스텔라루멘',
  'EOS': '이오스',
  'XTZ': '테조스',
  'MATIC': '폴리곤',
  'AAVE': '에이브',
  'ATOM': '코스모스',
  'MKR': '메이커',
  'COMP': '컴파운드',
  'SNX': '신세틱스',
  'SUSHI': '스시스왑',
  'YFI': ' yearn.finance',
  'MANA': '디센트럴랜드',
  'SAND': '더샌드박스',
  'AXS': '엑시인피니티',
  'SHIB': '시바이누',
  'CRO': '크로노스',
  'FTT': 'FTX토큰',
  'ETC': '이더리움클래식'
};

const allRows = ref([]);
const dailyChanges = ref({});

/* SEARCH */
const searchQuery = ref('');

const filteredRows = computed(() => {
  if (!searchQuery.value) {
    return allRows.value;
  }
  const query = searchQuery.value.toLowerCase();
  return allRows.value.filter(row =>
    row.name.toLowerCase().includes(query) ||
    row.code.toLowerCase().includes(query)
  );
});

const processedRows = computed(() => {
  return filteredRows.value.map(row => {
    const fullSymbol = `BINANCE:${row.code}USDT`; // Construct the full symbol
    const latestTrade = stockStore.latestTrades[fullSymbol];
    const dailyChange = dailyChanges.value[row.code];
    
    let price = '-';
    if (row.status === 'ACTIVE' && latestTrade) {
      price = latestTrade.p;
    }
    
    let change = '-';
    if (row.status === 'ACTIVE' && dailyChange !== undefined) {
      const dp = dailyChange.dp;
      change = `${dp > 0 ? '+' : ''}${dp.toFixed(2)}%`;
    }

    return {
      ...row,
      fullSymbol: fullSymbol, // Add fullSymbol for navigation
      price: price,
      change: change
    };
  });
});

/* PAGINATION */
const rowsPerPage = 7;
const currentPage = ref(1);

const totalPages = computed(() =>
    Math.ceil(processedRows.value.length / rowsPerPage)
);

const paginatedRows = computed(() => {
  const start = (currentPage.value - 1) * rowsPerPage;
  const end = start + rowsPerPage;
  return processedRows.value.slice(start, end);
});

const getChangeClass = (change) => {
  if (typeof change === 'string' && change.startsWith('+')) {
    return 'positive-change';
  } else if (typeof change === 'string' && change.startsWith('-')) {
    return 'negative-change';
  }
  return '';
};

// Function to navigate to stock detail page
const goToStockDetail = (symbol) => {
  router.push({ name: 'stock-detail', params: { symbol: symbol } });
};

// Fetch data on component mount
onMounted(async () => {
    try {
        const [stocksResponse, changesResponse] = await Promise.all([
            getAllStocks(),
            getDailyPriceChanges()
        ]);

        if (stocksResponse.success && stocksResponse.data) {
            allRows.value = stocksResponse.data.map(stock => ({
                code: stock.ticker,
                name: tickerToKoreanName[stock.ticker] || stock.name,
                market: stock.market,
                status: stock.status,
            }));
        }

        if (changesResponse.success && changesResponse.data) {
            const changesMap = changesResponse.data.reduce((acc, item) => {
                acc[item.ticker.toUpperCase()] = { dp: item.dp };
                return acc;
            }, {});
            dailyChanges.value = changesMap;
        }
    }
    catch (error) {
        console.error("Failed to fetch initial stock data:", error);
    }
});
</script>
