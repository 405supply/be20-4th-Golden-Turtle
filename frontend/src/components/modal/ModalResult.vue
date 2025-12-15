<template>
  <div class="modal-overlay" @click.self="$emit('close')">
    <div class="modal-box">
      <button class="modal-close" @click="$emit('close')">✖</button>

      <h2 class="modal-title">모의 투자 결과</h2>

      <div class="result-table">
        <div class="table-header">
          <span>등수</span>
          <span>닉네임</span>
          <span>총 금액</span>
          <span>총 수익률</span>
        </div>

        <div class="table-row">
          <span>{{ result.rank }}등</span>
          <span>{{ result.nickname }}</span>
          <span>$ {{ format(result.finalAsset) }}</span>
          <span>{{ result.totalReturn }}%</span>
        </div>
      </div>

      <p class="auto-close-text">
        {{ countdown }}초 후 메인 화면으로 이동합니다…
      </p>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from "vue";

const props = defineProps({ result: Object });

const format = (num) => {
  if (!num) return "0";
  return num.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ",");
};

const countdown = ref(3);
let interval = null;

onMounted(() => {
  interval = setInterval(() => {
    if (countdown.value > 1) {
      countdown.value--;
    } else {
      clearInterval(interval);
    }
  }, 1000);
});

onUnmounted(() => {
  clearInterval(interval);
});
</script>

<style scoped>
.modal-overlay {
  position: fixed;
  inset: 0;
  background: rgba(0,0,0,0.6);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 999;
}

.modal-box {
  width: 600px;
  background: #2b2518;
  border: 3px solid #c6a24a;
  border-radius: 12px;
  padding: 40px;
  position: relative;
}

.modal-close {
  position: absolute;
  top: 10px;
  right: 14px;
  background: none;
  border: none;
  color: white;
  font-size: 22px;
  cursor: pointer;
}

.modal-title {
  text-align: center;
  font-size: 28px;
  color: white;
  margin-bottom: 30px;
}

.result-table {
  border: 2px solid #c6a24a;
  border-radius: 8px;
  overflow: hidden;
}

.table-header,
.table-row {
  display: grid;
  grid-template-columns: 1fr 2fr 2fr 2fr;
  padding: 14px;
  color: white;
}

.table-header {
  background: rgba(255, 215, 0, 0.15);
}

/* 🔥 자동 이동 안내 문구 */
.auto-close-text {
  margin-top: 20px;
  text-align: center;
  color: #f6c749;
  font-size: 14px;
  opacity: 0.9;
}
</style>
