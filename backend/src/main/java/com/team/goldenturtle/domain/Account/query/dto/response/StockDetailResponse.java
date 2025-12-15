package com.team.goldenturtle.domain.Account.query.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class StockDetailResponse {
    private StockDTO stock;
}
