<template>
  <div v-if="isOpen" class="modal-overlay" @click.self="closeModal">
    <div class="stock-add-modal">
      <div class="modal-header">
        <h3 class="modal-title">종목 추가</h3>
        <button class="close-btn" @click="closeModal">X</button>
      </div>
      <div class="modal-body">
        <div class="input-container">
          <input
              type="text"
              v-model="tickerCode"
              @keyup.enter="addStock"
              placeholder="추가할 종목의 티커(심볼)를 입력하세요"
              class="ticker-input"
              ref="tickerInput"
          >
        </div>
        <p v-if="isDuplicate" class="warning-text">이미 목록에 존재하는 종목입니다.</p>
        <button
            :disabled="!tickerCode.trim() || isDuplicate"
            class="add-stock-action-btn"
            @click="addStock"
        >
          종목 추가
        </button>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'AddStockModal',
  props: {
    isOpen: {
      type: Boolean,
      required: true,
    },
    existingStocks: {
      type: Array,
      default: () => []
    }
  },
  data() {
    return {
      tickerCode: '',
    };
  },
  computed: {
    isDuplicate() {
      if (!this.tickerCode) return false;
      const code = this.tickerCode.toUpperCase().trim();
      return this.existingStocks.some(stock => stock.code === code);
    }
  },
  methods: {
    closeModal() {
      this.$emit('update:isOpen', false);
      this.tickerCode = ''; // 모달 닫을 때 입력값 초기화
    },
    addStock() {
      const symbol = this.tickerCode.trim().toUpperCase();
      if (!symbol || this.isDuplicate) return;

      // 부모에게 심볼(string)을 직접 전달
      this.$emit('add-stock', symbol);
      this.closeModal();
    },
    focusInput() {
      // ref를 사용하여 input 요소에 접근
      this.$nextTick(() => {
        if (this.$refs.tickerInput) {
          this.$refs.tickerInput.focus();
        }
      });
    }
  },
  watch: {
    isOpen(newVal) {
      if (newVal) {
        this.tickerCode = ''; // 모달이 열릴 때마다 입력값 초기화
        this.focusInput(); // 모달이 열리면 자동으로 input에 포커스
      }
    }
  }
};
</script>

<style scoped>
/*
 * =======================================================
 * 1. 모달 오버레이 및 중앙 정렬
 * =======================================================
 */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.7);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.stock-add-modal {
  width: 500px;
  height: 280px;
  background-color: #2d291f;
  border-radius: 12px;
  border: 2px solid #5C4F2B;
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.5);
  color: #ffffff;
  padding: 30px;
  display: flex;
  flex-direction: column;
}

/*
 * =======================================================
 * 2. 모달 헤더 및 본문
 * =======================================================
 */
.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;

}
/* ... (modal-title, close-btn 스타일 유지) ... */

.modal-body {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 10px;
  padding-top: 5px;
}
.close-btn {
  /* 현재 흰색 배경으로 보이는 X 버튼을 투명 또는 테마색으로 변경 */
  background: none; /* 배경 제거 */
  border: none;
  color: #ECB20F; /* X 글자색을 테마색(노란색 계열)으로 변경 */
  font-size: 24px;
  cursor: pointer;
  line-height: 1;
  padding: 0;
  transition: color 0.2s;
}

.close-btn:hover {
  color: #ffffff; /* 마우스 오버 시 흰색으로 변경 */
}

/*
 * =======================================================
 * 3. 검색 입력 및 버튼
 * =======================================================
 */
.input-container {
  display: flex;
  width: 80%;
  border: 1px solid #504a3d;
  border-radius: 10px;
  background-color: #4a4437;
  overflow: hidden;
}

.ticker-input {
  padding: 8px 15px;
  flex-grow: 1;
  border: none;
  background-color: transparent;
  color: white;
  font-size: 16px;
  outline: none;
}
.ticker-input::placeholder {
  color: #a0a0a0; /* 회색 계열로 변경하여 가독성 확보 */
  opacity: 1; /* 일부 브라우저에서 투명도가 적용되는 것을 방지 */
}
.ticker-input::placeholder {
  color: #a0a0a0; /* 회색 계열로 변경하여 가독성 확보 */
  opacity: 1; /* 일부 브라우저에서 투명도가 적용되는 것을 방지 */
}

/*
 * =======================================================
 * 4. 결과 및 추가 버튼
 * =======================================================
 */

.warning-text {
  color: #ff8a80;
  font-weight: bold;
  margin: 3px 0 0 0;
}

.add-stock-action-btn {
  background-color: #ECB20F;
  color: #2d291f;
  border: none;
  padding: 10px 30px;
  border-radius: 6px;
  font-weight: bold;
  cursor: pointer;
  width: 80%;
  margin-top: 10px;
  transition: background-color 0.2s, opacity 0.2s;
}

.add-stock-action-btn:hover:not(:disabled) {
  background-color: #F2CA57;
}

.add-stock-action-btn:disabled {
  background-color: #706b60;
  cursor: not-allowed;
  opacity: 0.6;
}
</style>