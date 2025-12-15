<script setup>
import { onMounted } from 'vue'
import Pagination from '@/components/common/paging/Pagination.vue'
import { useAccountStore } from '@/stores/Account.js'
import { storeToRefs } from 'pinia'
import '@/assets/stock/StockList.css'

const accountStore = useAccountStore()

const { page, totalPages, holdings } = storeToRefs(accountStore)

const formatMoney = (v) => {
  if (v == null) return '-'
  const num = Number(v)
  if (Number.isNaN(num)) return String(v)
  return num.toLocaleString()
}

const loadPage = (newPage = 1) => {
  accountStore.loadHoldings(newPage)
}

onMounted(() => {
  loadPage(1)
})
</script>

<template>
  <div class="gt-box">

    <div class="page-header">
      <h1 class="page-title">Portfolio</h1>
    </div>

    <div class="gt-box">
      <div class="stock-list-container">
        <table class="stock-table">
          <thead>
          <tr>
            <th>종목</th>
            <th>수량</th>
            <th>총 매수액</th>
            <th>평균단가</th>
          </tr>
          </thead>
          <tbody>
          <tr v-for="t in holdings" :key="t.holdingId">
            <td>{{ t.stock.ticker }}</td>
            <td>{{ t.quantity }}</td>
            <td>{{ formatMoney(t.totalAmount) }}</td>
            <td>{{ formatMoney(t.avgPrice) }}</td>
          </tr>
          <tr v-if="!holdings || holdings.length === 0">
            <td colspan="4" style="text-align: center;">
              조회된 보유 주식이 없습니다.
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
