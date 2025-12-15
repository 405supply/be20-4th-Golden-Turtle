<!-- src/components/AuthCard.vue -->
<template>
  <div class="card">
    <slot />
  </div>
</template>

<style scoped>
.card {
  width: 420px;
  padding: 40px;
  background: rgba(56, 47, 31, 0.95);
  border-radius: 10px;
  border: 1px solid rgba(56, 47, 31, 0.95);
}

@media (max-width: 768px) {
  .card {
    width: 90%;
    padding: 20px;
  }
}
</style>
