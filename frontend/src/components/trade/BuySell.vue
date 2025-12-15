  <template>
    <div class="order-form-container">
      <div class="top-toggle-buttons">
        <button
            :class="['toggle-btn', { 'active-buy': currentMode === '매수' }]"
            @click="changeMode('매수')"
        >
          매수
        </button>
        <button
            :class="['toggle-btn', { 'active-sell': currentMode === '매도' }]"
            @click="changeMode('매도')"
        >
          매도
        </button>
      </div>

      <div class="input-fields-area">

        <div class="form-group">
          <label>주문 유형</label>
          <input type="text" value="시장가" disabled class="market-price-display" />
        </div>

        <div class="form-group">
          <label>수량</label>
          <input
              type="number"
              v-model.number="orderQuantity"
              min="1"
              placeholder="주문 수량 입력"
              class="quantity-input"
          />
        </div>

        <div class="form-group">
          <label>가격 ($)</label>
          <input
              type="text"
              :value="formattedAmount"
              disabled
              class="calculated-amount-display"
          />
        </div>

      </div>

      <button
          :class="['execute-btn', currentMode === '매수' ? 'buy-button' : 'sell-button']"
          @click="submitOrder"
      >
        {{ currentMode }}
      </button>
    </div>
  </template>

  <script setup>
  import { ref, computed } from 'vue';
  import { useTradeStore } from '@/stores/trade'; // tradeStore 임포트
  import { useAccountStore } from '@/stores/Account';

  const props = defineProps({
    currentPrice: {
      type: Number,
      required: true,
      default: 0
    },
    stockSymbol: {
      type: String,
      required: false
    }
  });

  const tradeStore = useTradeStore(); // tradeStore 사용
  const accountStore = useAccountStore(); // accountStore 사용

  const currentMode = ref('매수'); // '매수' 또는 '매도'
  const orderQuantity = ref(0); // 주문 수량

  const calculatedAmount = computed(() => {
    const quantity = parseFloat(orderQuantity.value) || 0;
    return quantity * props.currentPrice;
  });

  const formattedAmount = computed(() => {
    // toLocaleString()이 숫자에만 적용되도록 수정
    return calculatedAmount.value.toFixed(2).toLocaleString('en-US', {minimumFractionDigits: 2, maximumFractionDigits: 2});
  });

  function changeMode(mode) {
    currentMode.value = mode;
  }

  async function submitOrder() {
    if (orderQuantity.value <= 0) {
      alert('주문 수량은 0보다 커야 합니다.');
      return;
    }
    if (!props.stockSymbol) {
      alert('종목을 선택해주세요.');
      return;
    }

    const ticker = props.stockSymbol;
    const quantity = orderQuantity.value;

    try {
      let success = false;
      if (currentMode.value === '매수') {
        success = await tradeStore.buyStock(ticker, quantity);
      } else { // 매도
        success = await tradeStore.sellStock(ticker, quantity);
      }

      if (success) {
        alert(`${currentMode.value} 주문이 성공적으로 접수되었습니다.\n${ticker} ${quantity}개, 총 금액: ${formattedAmount.value}`);
        orderQuantity.value = 0; // 주문 성공 후 수량 초기화
        accountStore.loadCashBalance(); // 잔고 업데이트
      } else {
        // tradeStore.buyStock/sellStock에서 이미 에러를 throw하므로 여기에 도달하지 않음
        // 하지만 혹시 모를 경우를 대비
        alert(`${currentMode.value} 주문 실패: 알 수 없는 오류`);
      }
    } catch (error) {
      console.error('매수 중 오류 발생:', error);
      alert(`${currentMode.value} 주문 실패: ${error.message || '금액이 부족하여 매수할 수 없습니다.'}`);
    }
  }
  </script>

  <style scoped>
  /* 기본적인 스타일링 */
  .order-form-container {
    width: 100%;
    padding: 10px; /* Reduced padding */
    border: 1px solid #5C4F2B;
    background-color: #2A2515; /* 이미지의 어두운 배경색 반영 */
    border-radius: 8px;
    color: #f0f0f0;
    display: flex;
    flex-direction: column;
  }

  .input-fields-area {
      flex-grow: 1;
  }

  /* 상단 토글 버튼 스타일 */
  .top-toggle-buttons {
    display: flex;
    margin-bottom: 10px; /* Reduced margin */
    gap: 10px;
  }
  .toggle-btn {
    flex: 1;
    padding: 8px 0; /* Reduced padding */
    border: none;
    border-radius: 6px;
    font-size: 14px; /* Reduced font size */
    font-weight: bold;
    cursor: pointer;
    background-color: #555;
    color: #bbb;
    transition: background-color 0.3s;
  }
  .active-buy {
    background-color: #22C55E; /* 초록색 */
    color: white;
    border: 1px solid #22C55E;
  }
  .active-sell {
    background-color: #DC2626; /* 빨간색 */
    color: white;
    border: 1px solid #DC2626;
  }

  /* 입력 필드 그룹 스타일 */
  .form-group {
    margin-bottom: 10px; /* Reduced margin */
  }
  .form-group label {
    display: block;
    font-size: 12px; /* Reduced font size */
    color: #ccc;
    margin-bottom: 3px; /* Reduced margin */
  }
  .form-group input {
    width: 100%;
    padding: 8px; /* Reduced padding */
    border: 1px solid #5C4F2B;
    border-radius: 4px;
    box-sizing: border-box;
    background-color: #332C19; /* 이미지의 어두운 입력 필드 배경색 반영 */
    color: white;
    font-size: 14px; /* Reduced font size */
  }

  /* 읽기 전용 필드 (Disabled) 스타일 */
  .form-group input:disabled {
    background-color: #332C19; /* 비활성화된 필드는 더 어둡게 */
    color: #ddd;
    cursor: default;
  }

  /* 하단 실행 버튼 스타일 */
  .execute-btn {
    width: 100%;
    padding: 10px 0; /* Reduced padding */
    border: none;
    border-radius: 6px;
    font-size: 16px; /* Reduced font size */
    font-weight: bold;
    cursor: pointer;
    margin-top: 10px; /* Reduced margin */
    transition: background-color 0.3s;
  }
  .buy-button {
    background-color: #22C55E; /* 매수 - 초록색 */
    color: white;
  }
  .sell-button {
    background-color: #DC2626; /* 매도 - 빨간색 */
    color: white;
  }
  </style>