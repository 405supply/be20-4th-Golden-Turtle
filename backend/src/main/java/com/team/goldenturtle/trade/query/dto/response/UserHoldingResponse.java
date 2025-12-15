package com.team.goldenturtle.trade.query.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
@AllArgsConstructor
public class UserHoldingResponse {
    private String ticker;
    private String stockName;
    private BigDecimal quantity;
    private BigDecimal avgPrice;    // 평단가
    private BigDecimal currentPrice;    // 현재가
    private BigDecimal profitAndLoss;   // 평가 손익(계산된 값)
    private BigDecimal returnRate;      // 수익률(계산된 값)


}
