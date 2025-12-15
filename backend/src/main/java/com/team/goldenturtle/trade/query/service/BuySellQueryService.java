package com.team.goldenturtle.trade.query.service;

import com.team.goldenturtle.domain.Account.query.dto.request.AccountSearchRequest;
import com.team.goldenturtle.domain.Account.query.dto.response.HoldingDTO;
import com.team.goldenturtle.domain.Account.query.dto.response.HoldingListResponse;
import com.team.goldenturtle.domain.Account.query.service.AccountQueryService;
import com.team.goldenturtle.trade.command.application.service.RealtimeStockPriceProvider;
import com.team.goldenturtle.trade.query.dto.response.UserHoldingListResponse;
import com.team.goldenturtle.trade.query.dto.response.UserHoldingResponse; // UserHoldingResponse 임포트
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BuySellQueryService {

    private final AccountQueryService accountQueryService;
    private final RealtimeStockPriceProvider realtimeStockPriceProvider;

    public UserHoldingListResponse getMyHoldings(AccountSearchRequest request) {
        // 1. 기존 AccountQueryService를 호출하여 DB 데이터를 가져옵니다.
        HoldingListResponse initialResponse = accountQueryService.getHoldings(request);
        List<HoldingDTO> holdingDTOs = initialResponse.getHoldings();

        // 2. DB 데이터(HoldingDTO)를 최종 응답 DTO(UserHoldingResponse)로 변환합니다.
        List<UserHoldingResponse> userHoldings = holdingDTOs.stream()
                .map(holding -> {
                    // 3. 실시간 가격을 가져옵니다.
                     // 실시간 가격 정보가 없으면 0으로 처리
                    BigDecimal currentPrice = realtimeStockPriceProvider
                            .getPrice(holding.getStock().getTicker())
                            .orElse(BigDecimal.ZERO);

                    // 4. 수익률과 평가손익을 계산합니다.
                    BigDecimal avgPrice = holding.getAvgPrice();
                    BigDecimal quantity = holding.getQuantity();
                    BigDecimal profitAndLoss = BigDecimal.ZERO;
                    BigDecimal returnRate = BigDecimal.ZERO;

                    if (avgPrice != null && avgPrice.compareTo(BigDecimal.ZERO) > 0) {
                        profitAndLoss = (currentPrice.subtract(avgPrice)).multiply(quantity);
                        returnRate = (currentPrice.divide(avgPrice, 4, RoundingMode.HALF_UP))
                                .subtract(BigDecimal.ONE).multiply(BigDecimal.valueOf(100));
                    }

                    // 5. 최종 UserHoldingResponse DTO를 생성합니다.
                    return UserHoldingResponse.builder()
                            .ticker(holding.getStock().getTicker())
                            .stockName(holding.getStock().getName())
                            .quantity(quantity)
                            .avgPrice(avgPrice)
                            .currentPrice(currentPrice)
                            .profitAndLoss(profitAndLoss)
                            .returnRate(returnRate)
                            .build();
                })
                .collect(Collectors.toList());

        // 6. 최종 응답 객체를 만들어 반환합니다.
        return UserHoldingListResponse.builder()
                .holdings(userHoldings)
                .pagination(initialResponse.getPagination())
                .build();
    }
}
