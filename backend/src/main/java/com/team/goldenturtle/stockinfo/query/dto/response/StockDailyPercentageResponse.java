package com.team.goldenturtle.stockinfo.query.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class StockDailyPercentageResponse {
    private String ticker;
    private Double dp;
    private Long timestamp;
}
