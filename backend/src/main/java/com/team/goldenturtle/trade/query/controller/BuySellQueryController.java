package com.team.goldenturtle.trade.query.controller;

import com.team.goldenturtle.auth.command.application.service.CustomUserDetails;
import com.team.goldenturtle.common.dto.ApiResponse;
import com.team.goldenturtle.domain.Account.query.dto.request.AccountSearchRequest;
import com.team.goldenturtle.trade.query.dto.response.UserHoldingListResponse;
import com.team.goldenturtle.trade.query.service.BuySellQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/buysell")
@RequiredArgsConstructor
public class BuySellQueryController {

    private final BuySellQueryService buySellQueryService;

    @GetMapping("/holdings")
    public ApiResponse<UserHoldingListResponse> getMyHoldings(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam("gameSessionId") Long gameSessionId,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10")int size
    ) {
        AccountSearchRequest request = AccountSearchRequest.builder()
                .userId(userDetails.getUserId())
                .gameSessionId(gameSessionId)
                .page(page)
                .size(size)
                .build();

        UserHoldingListResponse response = buySellQueryService.getMyHoldings(request);

        return ApiResponse.success(response);
    }
}
