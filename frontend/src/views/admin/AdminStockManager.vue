<template>
  <Header />
  <div class="admin-page-container">

    <div class="content-header">
      <h2 class="page-title">종목 관리</h2>
      <!-- '종목 추가' 버튼 제거됨 -->
    </div>

    <div class="stock-list-wrapper">
      <table class="stock-table">
        <thead>
        <tr>
          <th
              v-for="header in headers"
              :key="header.key"
              @click="sortBy(header.key)"
              :class="{ 'active-sort': sortKey === header.key }"
          >
            {{ header.label }}
            <span class="sort-icon" v-if="header.sortable !== false">
              {{ sortKey === header.key ? (sortOrder === 1 ? '▲' : '▼') : '⮁' }}
            </span>
          </th>
        </tr>
        </thead>
        <tbody>
        <tr v-for="(item, index) in displayedStocks" :key="index">
          <td>{{ item.code }}</td>
          <td>{{ item.name }}</td>
          <td>{{ item.market }}</td>
          <td>{{ item.date }}</td>
          <td>{{ item.isOperate ? '운영' : '미운영' }}</td>
          <td>
            <button v-if="!item.isOperate" @click="handleActivateStock(item.code)" class="action-btn activate-btn">활성화</button>
            <button v-else @click="handleDeactivateStock(item.code)" class="action-btn deactivate-btn">비활성화</button>
          </td>
        </tr>
        </tbody>
      </table>
    </div>

    <div class="table-footer">
      <span class="info-text">총 {{ stockStore.stocks.length }}개 종목</span>
      <div class="pagination">
        <button @click="changePage(currentPage - 1)" :disabled="currentPage === 1">&lt;</button>
        <span>{{ currentPage }} / {{ totalPages }}</span>
        <button @click="changePage(currentPage + 1)" :disabled="currentPage === totalPages">&gt;</button>
      </div>
    </div>
  </div>
  <!-- AddStockModal 제거됨 -->
</template>
<script>
import Header from "@/components/layout/header/Header.vue";
import { useStockStore } from "@/stores/stock"; // Pinia 스토어 임포트


export default {
  name: 'AdminStockManager',
  components: {Header}, // AddStockModal 제거
  setup() {
    const stockStore = useStockStore(); // 스토어 사용
    return { stockStore }; // 템플릿에서 스토어에 접근할 수 있도록 노출
  },
  data() {
    return {
      // isModalOpen 제거
      // 테이블 헤더 정보 (key는 데이터 객체의 속성 이름과 일치해야 함)
      headers: [
        { key: 'code', label: '종목 코드' },
        { key: 'name', label: '종목 이름' },
        { key: 'market', label: '종목 시장' },
        { key: 'date', label: '종목 등록일자' },
        { key: 'isOperate', label: '운영 여부' }, // data의 isOperate와 일치시킴
        { key: 'manage', label: '관리', sortable: false }, // '관리' 열 추가
      ],
      // stockList는 Pinia 스토어에서 관리하므로 여기서는 삭제
      // 정렬 및 페이지네이션 상태 추가
      sortKey: 'date',
      sortOrder: -1, // -1: 내림차순, 1: 오름차순 (기본: 최신 등록일 기준 내림차순)
      itemsPerPage: 6, // 한 페이지에 8개 항목 표시
      currentPage: 1,
    };
  },
  computed: {
    // Pinia 스토어의 stocks를 사용
    currentStocks() {
      return this.stockStore.stocks;
    },
    // 1. 정렬된 전체 목록을 반환
    sortedStocks() {
      let sortedList = [...this.currentStocks]; // 스토어의 stocks 사용
      const key = this.sortKey;
      const order = this.sortOrder;

      if (key) {
        sortedList.sort((a, b) => {
          // 'manage' 열은 정렬에서 제외
          if (key === 'manage') {
            return 0;
          }

          const aVal = a[key];
          const bVal = b[key];

          // Boolean 타입('운영 여부') 정렬 처리
          if (typeof aVal === 'boolean') {
            if (aVal === bVal) return 0;
            // true(운영)를 위로 올리려면 -1, 내리려면 1
            return aVal ? -1 * order : 1 * order;
          }

          if (aVal < bVal) return -1 * order;
          if (aVal > bVal) return 1 * order;
          return 0;
        });
      }
      return sortedList;
    },
    // 2. 현재 페이지에 표시될 8개 항목만 반환 (페이지네이션)
    displayedStocks() {
      const start = (this.currentPage - 1) * this.itemsPerPage;
      const end = start + this.itemsPerPage;
      return this.sortedStocks.slice(start, end);
    },
    // 3. 총 페이지 수 계산
    totalPages() {
      return Math.ceil(this.currentStocks.length / this.itemsPerPage); // 스토어의 stocks 사용
    }
  },
  methods: {
    // openAddModal 및 handleStockAdded 제거
    async handleActivateStock(symbol) {
      try {
        await this.stockStore.activateStock(symbol);
        console.log(`Stock with symbol ${symbol} activated successfully.`);
      } catch (error) {
        console.error(`Error activating stock with symbol ${symbol}:`, error);
        alert(`종목 활성화 실패: ${error.message}`);
      }
    },
    async handleDeactivateStock(symbol) {
      try {
        await this.stockStore.deactivateStock(symbol);
        console.log(`Stock with symbol ${symbol} deactivated successfully.`);
      } catch (error) {
        console.error(`Error deactivating stock with symbol ${symbol}:`, error);
        alert(`종목 비활성화 실패: ${error.message}`);
      }
    },
    //  정렬 기준 변경 기능 (sortBy)
    sortBy(key) {
      // 'manage' 열은 정렬에서 제외
      if (key === 'manage') {
        return;
      }
      if (this.sortKey === key) {
        this.sortOrder = this.sortOrder * -1; // 현재 기준이면 순서 반전
      } else {
        this.sortKey = key;
        this.sortOrder = 1; // 새로운 기준이면 오름차순으로 시작
      }
      this.currentPage = 1; // 정렬 기준 변경 시 1페이지로 이동
    },
    //  페이지 변경 기능 (changePage)
    changePage(page) {
      if (page >= 1 && page <= this.totalPages) {
        this.currentPage = page;
      }
    }
  },
  created() {
    this.stockStore.fetchStockList(); // 컴포넌트 생성 시 종목 목록 로드
  }
};
</script>

<style scoped>
/*
 * =======================================================
 * 1. 기본 레이아웃 및 테마 설정
 * =======================================================
 */
.admin-page-container {
  padding: 30px 40px;
  background-color: #3a3528;
  color: #ffffff;
  min-height: 100vh;
}

/* 헤더와 버튼 영역 */
.content-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 25px;
  flex-shrink: 0;
  width: 75.7%;
  max-width: 1090px;
  margin-left: auto;
  margin-right: auto;
}

.page-title {
  font-size: 24px;
  font-weight: 700;
  color: #ffffff;
  margin: 0;
}

/*
 * =======================================================
 * 2. 테이블 영역 및 스크롤 관리
 * =======================================================
 */

.stock-list-wrapper {
  width: 100%;
  max-width: 1090px;
  height: 450px;
  overflow-y: auto;
  border: 3px solid #5C4F2B;
  border-radius: 10px;
  background-color: #2A2515;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.3);
  margin: 0 auto;
}

.stock-table {
  width: 100%;
  border-collapse: collapse;
}

/* 테이블 헤더 스타일 */
.stock-table thead {
  background-color: #4a4437;
  color: #ffffff;
  position: sticky;
  top: 0;
  z-index: 10;
}

/* 정렬 기능 추가: 헤더에 커서와 활성화 스타일 적용 */
.stock-table th {
  padding: 15px 12px;
  text-align: left;
  font-weight: 600;
  border-bottom: 1px solid #504a3d;
  cursor: pointer; /* 클릭 가능 표시 */

}

.stock-table th.active-sort {
  background-color: #403b30; /* 현재 정렬 기준 강조 */
}

.sort-icon {
  margin-left: 8px;
  font-size: 10px;
  opacity: 0.8;
}

/* 테이블 바디 스타일 */
.stock-table tbody tr {
  border-bottom: 1px solid #5C4F2B;
  transition: background-color 0.1s;
}

.stock-table tbody tr:last-child {
  border-bottom: none;
}

.stock-table tbody tr:hover {
  background-color: #403b30;
}

.stock-table td {
  padding: 15px 12px;
  font-size: 14px;
}

/*
 * =======================================================
 * 4. 활성화/비활성화 버튼 스타일
 * =======================================================
 */
.action-btn {
  padding: 8px 12px;
  border: none;
  border-radius: 6px;
  font-weight: bold;
  cursor: pointer;
  transition: background-color 0.2s, color 0.2s;
  font-size: 13px;
  min-width: 70px; /* 버튼 최소 너비 설정 */
}

.activate-btn {
  background-color: #28a745; /* Green for activate */
  color: #ffffff;
}

.activate-btn:hover {
  background-color: #218838;
}

.deactivate-btn {
  background-color: #dc3545; /* Red for deactivate */
  color: #ffffff;
}

.deactivate-btn:hover {
  background-color: #c82333;
}


/*
 * =======================================================
 * 3. 테이블 하단 푸터 (페이지네이션)
 * =======================================================
 */
.table-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 14px;
  max-width: 1090px;
  margin: 15px auto 0 auto;

}

.pagination button {
  background: #504a3d;
  border: none;
  color: white;
  padding: 5px 10px;
  margin: 0 3px;
  border-radius: 4px;
  cursor: pointer;
}

/* 페이지네이션 버튼 비활성화 상태 추가 */
.pagination button:disabled {
  opacity: 0.5;
  cursor: default;
}

.sort-icon {
  margin-left: 8px;
  font-size: 15px;
  opacity: 0.8; /* 기본 투명도 */
  transition: opacity 0.2s;
}

/* 정렬이 활성화된 컬럼의 아이콘은 투명도 1로 강조 */
.stock-table th.active-sort .sort-icon {
  opacity: 1;
}

/* 정렬이 활성화되지 않은 컬럼의 아이콘은 투명도 0.4 정도로 낮춤 */
.stock-table th:not(.active-sort) .sort-icon {
  opacity: 0.4;
}
</style>