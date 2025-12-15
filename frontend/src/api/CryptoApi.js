import api from './axios';

export const getCryptoHistory = async (symbol) => {
    try {
        const response = await api.get(`/api/v1/history/${symbol}`);
        return response.data;
    } catch (error) {
        console.error(`Error fetching crypto history for ${symbol}:`, error);
        throw error;
    }
};