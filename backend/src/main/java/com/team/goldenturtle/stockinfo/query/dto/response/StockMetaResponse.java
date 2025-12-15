package com.team.goldenturtle.stockinfo.query.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockMetaResponse {
    @JsonProperty("h")
    BigDecimal highPrice;

    @JsonProperty("l")
    BigDecimal lowPrice;

    @JsonProperty("o")
    BigDecimal openPrice;

    @JsonProperty("pc")
    BigDecimal closePrice;

    @JsonProperty("dp")
    Double percentChange;

    @JsonProperty("t")
    Long timestamp;
}
