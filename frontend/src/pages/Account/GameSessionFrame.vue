<script setup>
import { onMounted } from 'vue'
import { useRouter } from 'vue-router'
import Pagination from '@/components/common/paging/Pagination.vue'
import { useAccountStore } from '@/stores/Account'
import { storeToRefs } from 'pinia'
import '@/assets/stock/StockList.css'

const router = useRouter()
const accountStore = useAccountStore()

const { sessions, page, totalPages } = storeToRefs(accountStore)

const formatMoney = (v) =>
    v == null ? '-' : Number(v).toLocaleString()

const formatPercent = (v) =>
    v == null ? '-' : `${Number(v).toFixed(2)}%`



const loadPage = (p = 1) => {
  accountStore.loadSessions(p)
}

const goToDetail = (sessionId) => {
  router.push(`/gamesession/${sessionId}/detail`)
}

onMounted(() => {
  loadPage(1)
})
</script>

<template>
  <div class="gt-box">

    <div class="page-header">
      <h1 class="page-title">GameSession</h1>
    </div>

    <div class="gt-box">

      <div class="stock-list-container">
        <table class="stock-table">
          <thead>
          <tr>
            <th>세션번호</th>
            <th>시작시간</th>
            <th>종료시간</th>
            <th>종료금액</th>
            <th>최종 수익률</th>
          </tr>
          </thead>
          <tbody>
          <tr
              v-for="s in sessions"
              :key="s.sessionId"
              class="clickable-row"
              @click="goToDetail(s.sessionId)"
          >
            <td>{{ s.sessionId }}</td>
            <td>{{ s.startedAt }}</td>
            <td>{{ s.endedAt }}</td>
            <td>{{ formatMoney(s.finalAsset) }}</td>
            <td>{{ formatPercent(s.totalReturn) }}</td>
          </tr>

          <tr v-if="sessions.length === 0">
            <td colspan="5" style="text-align:center">
              조회된 세션이 없습니다.
            </td>
          </tr>
          </tbody>
        </table>
      </div>

      <Pagination
          :page="page"
          :total-pages="totalPages"
          @update:currentPage="loadPage"
      />
    </div>
  </div>
</template>
