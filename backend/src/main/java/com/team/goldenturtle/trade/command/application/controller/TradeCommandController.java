package com.team.goldenturtle.trade.command.application.controller;

import com.team.goldenturtle.auth.command.application.service.CustomUserDetails;
import com.team.goldenturtle.common.dto.ApiResponse;
import com.team.goldenturtle.trade.command.application.dto.request.BuyStockCommandRequestDto;
import com.team.goldenturtle.trade.command.application.dto.request.SellStockCommandRequestDto;
import com.team.goldenturtle.trade.command.application.service.TradeCommandService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/buysell")
@RequiredArgsConstructor
public class TradeCommandController {

    private final TradeCommandService tradeCommandService;

    @PostMapping("/buy")
    public ApiResponse<Void> buyStock(
            @AuthenticationPrincipal CustomUserDetails userDetails,
                                      @RequestBody BuyStockCommandRequestDto request) {
        tradeCommandService.handleBuyCommand(userDetails.getUserId(), request);
        return ApiResponse.success(null);
    }

    @PostMapping("/sell")
    public ApiResponse<Void> sellStock(@AuthenticationPrincipal CustomUserDetails userDetails, @RequestBody SellStockCommandRequestDto request) {


        tradeCommandService.handleSellCommand(userDetails.getUserId(),request);
        return ApiResponse.success(null);
    }
}
