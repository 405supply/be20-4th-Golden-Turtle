import api from './axios.js'

export async function fetchGameSessionsByUserId(page = 1, size = 10) {
    const res = await api.get('api/v1/gamesessions', {
        params: {page, size },
    })
    console.log(res.data.data)
    return res.data.data
}

export async function fetchTradesBySessionId(gameSessionId, page = 1, size = 10) {
    const res = await api.get('api/v1/trades', {
        params: { gameSessionId, page, size },
    })
    console.log(res.data.data)
    return res.data.data
}

export async function fetchHoldingsByUserId(page = 1, size = 10) {
    const res = await api.get('api/v1/holdings', {
        params: {page, size },
    })
    console.log(res.data.data)
    return res.data.data
}

export async function fetchCashBalanceByUserId() {
    const res = await api.get('api/v1/balance')
    console.log(res.data.data)
    return res.data.data
}
