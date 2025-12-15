package com.team.goldenturtle.stockinfo.query.controller;

import com.team.goldenturtle.common.dto.ApiResponse;
import com.team.goldenturtle.stockinfo.query.service.StockInfoService;
import com.team.goldenturtle.trade.command.application.service.RealtimeStockPriceProvider;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class StockQueryController {

    private final StockInfoService stockInfoService;
    private final RealtimeStockPriceProvider realtimeStockPriceProvider;

    @GetMapping("/v1/stocks")
    public ResponseEntity<ApiResponse<?>> getAllStocks(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "7") int size
    ) {
        return ResponseEntity.ok(ApiResponse.success(stockInfoService.getAllStocks(page, size)));
    }

    @GetMapping("v1/stock/{symbol}")
    public ResponseEntity<ApiResponse<?>> getStock(@PathVariable String symbol) {
        return ResponseEntity.ok(ApiResponse.success(stockInfoService.getStockMeta(symbol)));
    }

    @GetMapping("v1/stock/dp")
    public ResponseEntity<ApiResponse<?>> getStocksDailyPercentage() {
        return ResponseEntity.ok(ApiResponse.success(stockInfoService.getStocksDailyPercentage()));
    }

    @GetMapping("/v1/history/{symbol}")
    public ResponseEntity<ApiResponse<?>> getCryptoHistory(@PathVariable("symbol") @NotBlank String symbol) {
        List<RealtimeStockPriceProvider.PriceEntry> history = realtimeStockPriceProvider.getRecentHistory(symbol);

        return ResponseEntity.ok(ApiResponse.success(history));
    }
}
