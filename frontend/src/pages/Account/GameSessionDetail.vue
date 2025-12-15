<script setup>
import { onMounted } from 'vue'
import { useRoute } from 'vue-router'
import Pagination from '@/components/common/paging/Pagination.vue'
import { useAccountStore } from '@/stores/Account.js'
import { storeToRefs } from 'pinia'
import '@/assets/stock/StockList.css'

const route = useRoute()
const accountStore = useAccountStore()

const { page, totalPages, trades } = storeToRefs(accountStore)

const formatMoney = (v) => {
  if (v == null) return '-'
  const num = Number(v)
  if (Number.isNaN(num)) return String(v)
  return num.toLocaleString()
}

const formatDateTime = (v) => {
  if (!v) return '-'
  return String(v).replace('T', ' ')
}

const loadPage = (newPage = 1) => {
  const sessionId = Number(route.params.sessionId)
  if (!sessionId) return
  accountStore.loadTrades(sessionId, newPage)
}

onMounted(() => {
  loadPage(1)
})
</script>

<template>
  <div class="gt-box">

    <div class="page-header">
      <h1 class="page-title">Trade</h1>
    </div>

    <div class="gt-box">
      <div class="stock-list-container">
        <table class="stock-table">
          <thead>
          <tr>
            <th>거래 ID</th>
            <th>종목 코드</th>
            <th>타입</th>
            <th>가격</th>
            <th>수량</th>
            <th>체결시간</th>
          </tr>
          </thead>
          <tbody>
          <tr v-for="t in trades" :key="t.tradeId">
            <td>{{ t.tradeId }}</td>
            <td>{{ t.stock.ticker }}</td>
            <td>{{ t.side }}</td>
            <td>{{ formatMoney(t.price) }}</td>
            <td>{{ t.quantity }}</td>
            <td>{{ formatDateTime(t.createdAt) }}</td>
          </tr>

          <tr v-if="!trades || trades.length === 0">
            <td colspan="6" style="text-align: center;">
              조회된 거래 내역이 없습니다.
            </td>
          </tr>
          </tbody>
        </table>
      </div>

      <Pagination
          :current-page="page"
          :total-pages="totalPages"
          @update:currentPage="loadPage"
      />
    </div>
  </div>
</template>
