<template>
  <div class="gt-box page-container">
    <h1 class="main-title">Market</h1>
    <StockListComponent />
  </div>
</template>

<script setup>
import StockListComponent from '@/components/stock/StockListComponent.vue';
</script>

<style scoped>
.page-container {
  padding: 2rem; /* Add some padding around the content */
  background-color: #2b2417; /* Dark background */
  min-height: calc(100vh - var(--header-height, 0px) - var(--footer-height, 0px)); /* Fill remaining height, adjust for header/footer if any */
  color: #f6c749; /* Default text color for the page */
}

.main-title {
  font-size: 36px;      /* 🔥 GameSession과 동일 */
  font-weight: 800;     /* 🔥 동일 */
  line-height: 1.2;
  margin-bottom: 20px;
  color: #f6c749;
  text-align: left;
  margin-top: 0;
}

</style>
