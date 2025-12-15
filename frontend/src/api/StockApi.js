import api from './axios';

export const getStockDetail = async (symbol) => {
    try {
        const response = await api.get(`/api/v1/stock/${symbol}`);
        return response.data;
    } catch (error) {
        console.error(`Error fetching stock detail for ${symbol}:`, error);
        throw error;
    }
};

export const getAllStocks = async () => {
    try {
        const response = await api.get(`/api/v1/stocks?size=100`);
        return response.data;
    } catch (error) {
        console.error("Error fetching all stocks:", error);
        throw error;
    }
};

export const getDailyPriceChanges = async () => {
    try {
        const response = await api.get('/api/v1/stock/dp');
        return response.data;
    } catch (error) {
        console.error("Error fetching daily price changes:", error);
        throw error;
    }
};
