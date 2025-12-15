import { defineStore } from 'pinia';
import { ref } from 'vue';
import api from '@/api/axios'; // axios 인스턴스 임포트

export const useTradeStore = defineStore('trade', () => {
    const userHoldings = ref([]);
    const pagination = ref({});
    const isTrading = ref(false); // 🔒 trade 락 추가

    const buyStock = async (ticker, quantity) => {
        if (isTrading.value) {
            console.warn('매수 처리 중입니다. 잠시만 기다려주세요.');
            return;
        }

        isTrading.value = true;

        try {
            const response = await api.post('/api/v1/buysell/buy', { ticker, quantity });

            if (response.data.success) {
                console.log(`Successfully bought ${quantity} of ${ticker}`);
                await fetchHoldings();
                return true;
            } else {
                console.error('API response indicates failure:', response.data);
                throw new Error(response.data.message || 'Unknown error');
            }
        } catch (error) {
            console.error(
                'Error buying stock:',
                error.response?.data?.message || error.message
            );
            throw error;
        } finally {
            isTrading.value = false; // 🔓 반드시 해제
        }
    };

    const sellStock = async (ticker, quantity) => {
        if (isTrading.value) {
            console.warn('매도 처리 중입니다. 잠시만 기다려주세요.');
            return;
        }

        isTrading.value = true;

        try {
            const response = await api.post('/api/v1/buysell/sell', { ticker, quantity });

            if (response.data.success) {
                console.log(`Successfully sold ${quantity} of ${ticker}`);
                await fetchHoldings();
                return true;
            } else {
                console.error('API response indicates failure:', response.data);
                throw new Error(response.data.message || 'Unknown error');
            }
        } catch (error) {
            console.error(
                'Error selling stock:',
                error.response?.data?.message || error.message
            );
            throw error;
        } finally {
            isTrading.value = false;
        }
    };

    const fetchHoldings = async (page = 0, size = 10) => {
        const gameSessionId = localStorage.getItem('gameSessionId');
        if (!gameSessionId) {
            console.log('보유 종목은 게임시작 후 확인 가능합니다.');
            return;
        }

        try {
            const response = await api.get('/api/v1/buysell/holdings', {
                params: { gameSessionId, page, size },
            });

            if (response.data.success && response.data.data) {
                userHoldings.value = response.data.data.holdings;
                pagination.value = response.data.data.pagination;
                console.log('User holdings fetched:', userHoldings.value);
                return true;
            } else {
                console.error('API response indicates failure or missing data:', response.data);
                throw new Error(response.data.message || 'Unknown error');
            }
        } catch (error) {
            console.error(
                'Error fetching holdings:',
                error.response?.data?.message || error.message
            );
            throw error;
        }
    };

    return {
        userHoldings,
        pagination,
        isTrading, // 👈 버튼 제어용으로 노출
        buyStock,
        sellStock,
        fetchHoldings,
    };
});
