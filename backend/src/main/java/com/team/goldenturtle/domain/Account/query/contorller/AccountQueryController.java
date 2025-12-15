package com.team.goldenturtle.domain.Account.query.contorller;

import com.team.goldenturtle.common.dto.ApiResponse;
import com.team.goldenturtle.domain.Account.query.dto.request.AccountSearchRequest;
import com.team.goldenturtle.domain.Account.query.dto.response.GameSessionListResponse;
import com.team.goldenturtle.domain.Account.query.dto.response.HoldingListResponse;
import com.team.goldenturtle.domain.Account.query.dto.response.TradeListResponse;
import com.team.goldenturtle.domain.Account.query.service.AccountQueryService;
import com.team.goldenturtle.auth.command.application.service.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import java.security.Principal;
import org.apache.ibatis.annotations.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class AccountQueryController {

    private final AccountQueryService accountQueryService;

    @GetMapping("/holdings")
    public ResponseEntity<ApiResponse<HoldingListResponse>> getAccount(
            AccountSearchRequest accountSearchRequest, Principal principal
    ){
        CustomUserDetails userDetails = (CustomUserDetails) ((Authentication) principal).getPrincipal();
        accountSearchRequest.setUserId(userDetails.getUserId());
        HoldingListResponse response = accountQueryService.getHoldings(accountSearchRequest);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/trades")
    public ResponseEntity<ApiResponse<TradeListResponse>> getTrades(
            AccountSearchRequest accountSearchRequest, Principal principal
    ){
        CustomUserDetails userDetails = (CustomUserDetails) ((Authentication) principal).getPrincipal();
        accountSearchRequest.setUserId(userDetails.getUserId());
        TradeListResponse response = accountQueryService.getTrades(accountSearchRequest);
        return ResponseEntity.ok(ApiResponse.success(response));
    }


    @GetMapping("/gamesessions")
    public ResponseEntity<ApiResponse<GameSessionListResponse>> getGameSessions(
            AccountSearchRequest accountSearchRequest, Principal principal
    ){
        CustomUserDetails userDetails = (CustomUserDetails) ((Authentication) principal).getPrincipal();
        accountSearchRequest.setUserId(userDetails.getUserId());
        GameSessionListResponse response = accountQueryService.getGameSessions(accountSearchRequest);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/balance")
    public ResponseEntity<ApiResponse<BigDecimal>> getBalance(Principal principal){
        CustomUserDetails userDetails = (CustomUserDetails) ((Authentication) principal).getPrincipal();
        BigDecimal balance = accountQueryService.getBalance(userDetails.getUserId());
        return ResponseEntity.ok(ApiResponse.success(balance));
    }
}
