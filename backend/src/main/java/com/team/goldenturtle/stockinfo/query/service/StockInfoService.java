package com.team.goldenturtle.stockinfo.query.service;

import com.team.goldenturtle.domain.Account.query.dto.response.StockDetailResponse;
import com.team.goldenturtle.stockinfo.query.dto.response.StockDailyPercentageResponse;
import com.team.goldenturtle.stockinfo.query.dto.response.StockListResponse;
import com.team.goldenturtle.stockinfo.query.dto.response.StockMetaResponse;

import java.util.List;

public interface StockInfoService {
    List<StockListResponse> getAllStocks(int limit, int offset);
    List<StockListResponse> getActiveStocks();
    StockMetaResponse getStockMeta(String ticker);
    List<StockDailyPercentageResponse> getStocksDailyPercentage();
}
