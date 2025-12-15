  <!-- src/components/SidebarHoldings.vue -->
  <script setup>
  import { computed, onMounted } from 'vue'
  import { useTradeStore } from '@/stores/trade.js'
  import { storeToRefs } from 'pinia'

  const tradeStore = useTradeStore()
  const { userHoldings } = storeToRefs(tradeStore)

  const visibleHoldings = computed(() =>
      userHoldings.value.filter(h => h.quantity > 0)
  )

  onMounted(() => {
    tradeStore.fetchHoldings()
  })
  </script>

  <template>
    <!-- 부모에서 <aside class="sidebar-wrapper">로 감싸서 사용 -->
    <section class="sidebar-holdings">

      <!-- 헤더 -->
      <div class="holding-header">
        <span>종목명</span>
        <span>수량</span>
        <span>평균단가</span>
      </div>

      <!-- 리스트 -->
      <div v-if="visibleHoldings.length" class="holding-list">
        <div
            v-for="item in visibleHoldings"
            :key="item.holdingId"
            class="holding-row"
        >
          <!-- 1열: 종목명 / 티커 -->
          <div>
            <p class="holding-name">{{ item.stockName }}</p>
            <p class="holding-ticker">{{ item.ticker }}</p>
          </div>

          <!-- 2열: 수량 -->
          <p class="holding-mid">
            {{ item.quantity }}
          </p>

          <!-- 3열: 평균단가 -->
          <p class="holding-price-final">
            $ {{ item.avgPrice.toFixed(2) }}
          </p>
        </div>
      </div>

      <!-- 빈 상태 -->
      <p v-else class="holding-empty">
        보유 중인 종목이 없습니다.
      </p>
    </section>
  </template>

  <style scoped>
  /* 사이드바 내부 카드 느낌 */
  .sidebar-holdings {
    /* 원래 있던 레이아웃 설정 유지 */
    display: flex;
    flex-direction: column;
    gap: 6px;

    /* balance-panel 스타일 입히기 */
    background: #3a3528;
    border-radius: 12px;
    padding: 12px 10px 8px; /* 살짝 위쪽 여유 있게 */
    box-sizing: border-box;
    box-shadow:
        inset 0 0 8px rgba(0,0,0,0.55),
        0 0 0 2px rgba(80,74,61,0.7);
  }

  .holding-header,
  .holding-row {
    display: grid;
    grid-template-columns: 1.1fr 0.9fr 1fr; /* 종목 / 수량 / 평균단가 비율 */
    column-gap: 8px;
    align-items: center;
    padding: 4px 0;
  }

  /* 헤더 스타일 */
  .holding-header {
    font-weight: 700;
    padding-bottom: 4px;
    margin-bottom: 6px;
    border-bottom: 2px solid #504a3d;
    font-size: 12px;
  }

  /* 헤더 정렬: 종목 왼쪽, 수량 가운데, 가격 오른쪽 */
  .holding-header span:nth-child(1) {
    text-align: left;
  }
  .holding-header span:nth-child(2) {
    text-align: center;
  }
  .holding-header span:nth-child(3) {
    text-align: right;
  }

  .holding-list {
    display: flex;
    flex-direction: column;
    gap: 4px;
  }

  /* 데이터 행들 */
  .holding-row {
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
    font-size: 12px;
  }

  /* 1열: 종목 정보 */
  .holding-name {
    margin: 0;
    font-weight: 600;
  }
  .holding-ticker {
    margin: 0;
    font-size: 11px;
    opacity: 0.75;
  }

  /* 2열: 수량 (센터 정렬) */
  .holding-mid {
    text-align: center;
  }

  /* 3열: 평균단가 (오른쪽 정렬) */
  .holding-price-final {
    text-align: right;
    font-weight: 600;
  }

  /* 빈 상태 텍스트 */
  .holding-empty {
    font-size: 12px;
    opacity: 0.7;
    margin: 4px 0 0;
  }
  </style>
