package com.team.goldenturtle.stockinfo.command.application.controller;

import com.team.goldenturtle.common.dto.ApiResponse;
import com.team.goldenturtle.stockinfo.command.application.service.StockManageService;
import com.team.goldenturtle.trade.command.application.service.RealtimeStockPriceProvider;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@Validated
@RequiredArgsConstructor
public class StockCommandController {

    private final StockManageService stockManageService;

    @PostMapping("/v1/crypto/{symbol}")
    public ResponseEntity<ApiResponse<?>> addCrypto(@PathVariable("symbol") @NotBlank String symbol) {
        stockManageService.addCrypto(symbol);

        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @DeleteMapping("/v1/crypto/{symbol}")
    public ResponseEntity<ApiResponse<?>> deleteCrypto(@PathVariable("symbol") @NotBlank String symbol) {
        stockManageService.deleteCrypto(symbol);

        return ResponseEntity.ok(ApiResponse.success(null));
    }
}
