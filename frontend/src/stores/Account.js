import { defineStore } from 'pinia'
import { ref } from 'vue'
import {fetchGameSessionsByUserId,
    fetchTradesBySessionId,
    fetchHoldingsByUserId,
    fetchCashBalanceByUserId} from '@/api/AccountApi.js'

export const useAccountStore = defineStore('account', () => {
    const page = ref(1)
    const totalPages = ref(1)
    const cashBalance = ref(1)
    const sessions = ref([])
    const trades = ref([])
    const holdings =ref([])

    const loadSessions = async (newPage = 1) => {
        try {
            const data = await fetchGameSessionsByUserId(newPage)

            sessions.value = data.gameSessions ?? []
            totalPages.value = data.pagination?.totalPages ?? 1
            page.value = data.pagination?.currentPage ?? newPage
        } catch (error) {
            console.error('게임세션을 불러올수 없습니다:', error)
        }
    }

    const loadTrades = async (sessionId,newPage = 1) => {
        try {
            const data = await fetchTradesBySessionId(sessionId, newPage)
            trades.value = data.trades ?? []
            totalPages.value = data.pagination?.totalPages ?? 1
            page.value = data.pagination?.currentPage ?? newPage
            console.log(trades.value)
        } catch (error) {
            console.error('거래내역을 불러올 수 없습니다:', error)
        }
    }

    const loadHoldings = async (newPage = 1) => {
        try {
            const data = await fetchHoldingsByUserId(newPage)
            holdings.value = (data.holdings ?? []).filter(h => h.quantity > 0)
            totalPages.value = data.pagination?.totalPages ?? 1
            page.value = data.pagination?.currentPage ?? newPage
            console.log(holdings.value)
        } catch (error) {
            console.error('거래내역을 불러올 수 없습니다:', error)

        }
    }

    const loadCashBalance = async () => {
        try {
            const data = await fetchCashBalanceByUserId()
            cashBalance.value = data ?? 0
            console.log(cashBalance.value)
        } catch (error) {
            console.error('거래내역을 불러올 수 없습니다:', error)
        }
    }
    return {
        page,
        totalPages,
        sessions,
        trades,
        holdings,
        cashBalance,
        loadTrades,
        loadSessions,
        loadHoldings,
        loadCashBalance
    }
})
