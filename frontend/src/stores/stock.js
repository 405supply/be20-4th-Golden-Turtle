import { ref, reactive } from 'vue'
import { defineStore } from 'pinia'
import * as StompJs from '@stomp/stompjs'
import SockJS from 'sockjs-client'
import api from '@/api/axios'; // axios 인스턴스 임포트
import { useAuthStore } from './auth'; // auth 스토어 임포트


export const useStockStore = defineStore('stock', () => {
  const latestTrades = reactive({})
  const stocks = ref([]);
  const isConnected = ref(false)
  let stompClient = null

  const connect = () => {
    const authStore = useAuthStore();
    // if (!authStore.accessToken) {
    //     console.error("[STOMP] 주식 소켓 연결 실패: 인증 토큰이 없습니다.");
    //     // TODO: 로그인 페이지로 리다이렉트하거나 사용자에게 로그인 요청 (Vue Router가 필요)
    //     return;
    // }

    if (stompClient?.active) {
      console.log('STOMP client is already active.')
      return
    }
    console.log('Connecting to STOMP server...')
    stompClient = new StompJs.Client({
      webSocketFactory: () => new SockJS('http://localhost:8080/stock'),
      connectHeaders: { // <-- 여기에 인증 헤더 추가
        Authorization: `Bearer ${authStore.accessToken}`,
      },
      reconnectDelay: 5000,
      debug: (str) => { /* console.log('STOMP DEBUG:', str) */ },
    })
    stompClient.onConnect = frame => {
      isConnected.value = true
      console.log('STOMP connected', frame)
      stompClient.subscribe('/topic/stock', msg => {
        try {
          const parsed = JSON.parse(msg.body)
          if (parsed.data && Array.isArray(parsed.data)) {
            for (const trade of parsed.data) {
              if (trade.s) {
                latestTrades[trade.s] = {
                  p: trade.p,
                  t: trade.t,
                  v: trade.v,
                  c: trade.c,
                }
              }
            }
          } else {
             console.warn('Received STOMP message with unexpected data structure:', parsed);
          }
        } catch (e) {
          console.error('STOMP message parse error', e)
        }
      })
    }
    stompClient.onStompError = frame => {
      isConnected.value = false
      console.error('STOMP error', frame)
    }
    stompClient.onWebSocketClose = () => {
        isConnected.value = false;
        console.log('STOMP WebSocket connection closed.');
    };
    stompClient.activate()
  }

  const disconnect = () => {
    if (stompClient) {
      stompClient.deactivate()
      console.log('STOMP client deactivated.')
    }
  }

  // fetch를 api.get으로 변경
  const fetchStockList = async () => {
    try {
      const response = await api.get('/api/v1/stocks', {
        params: { size: 1000 }
      });

      if (response.data.success && response.data.data) {
        stocks.value = response.data.data.map(stock => ({
            code: stock.ticker,
            name: stock.name,
            market: stock.market,
            date: stock.createdAt ? stock.createdAt.split('T')[0] : 'N/A',
            isOperate: stock.status === 'ACTIVE',
        }));
        console.log('Fetched stock list:', stocks.value);
      } else {
        console.error('API response indicates failure or missing data:', response.data);
        stocks.value = [];
      }
    } catch (error) {
      console.error('Error fetching stock list:', error.response?.data?.message || error.message);
      stocks.value = [];
    }
  };

  // fetch를 api.post로 변경
  const activateStock = async (rawSymbol) => {
    const symbol = `${rawSymbol.toUpperCase()}`;
    try {
      const response = await api.post(`/api/v1/crypto/${symbol}`);
      if (response.data.success) {
        console.log('Stock activated successfully for symbol:', symbol);
        await fetchStockList();
        return true;
      } else {
        console.error('API response indicates failure:', response.data);
        throw new Error(`API error: ${response.data.message || 'Unknown error'}`);
      }
    } catch (error) {
      console.error('Error activating stock:', error.response?.data?.message || error.message);
      throw new Error(error.response?.data?.message || error.message);
    }
  };

  // fetch를 api.delete로 변경
  const deactivateStock = async (rawSymbol) => {
    const symbol = `${rawSymbol.toUpperCase()}`;
    try {
      const response = await api.delete(`/api/v1/crypto/${symbol}`);
      if (response.data.success) {
        console.log('Stock deactivated successfully for symbol:', symbol);
        await fetchStockList();
        return true;
      } else {
        console.error('API response indicates failure:', response.data);
        throw new Error(`API error: ${response.data.message || 'Unknown error'}`);
      }
    } catch (error) {
      console.error('Error deactivating stock:', error.response?.data?.message || error.message);
      throw new Error(error.response?.data?.message || error.message);
    }
  }

  return {
    latestTrades,
    stocks,
    isConnected,
    connect,
    disconnect,
    fetchStockList,
    activateStock,
    deactivateStock
  }
});
