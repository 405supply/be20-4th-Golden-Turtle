<script setup>
import { onMounted } from 'vue'
import { useStockStore } from './stores/stock'

const stockStore = useStockStore()

onMounted(() => {
  // Connect to the WebSocket via the Pinia store when the app mounts.
  stockStore.connect()
})
</script>

<template>
  <RouterView />
</template>

<style>
body {
  margin: 0;
  background-color: #1f1b13;
}
</style>

<style scoped>

</style>
