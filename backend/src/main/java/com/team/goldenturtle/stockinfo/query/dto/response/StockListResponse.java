package com.team.goldenturtle.stockinfo.query.dto.response;

import com.team.goldenturtle.common.enums.StockStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class StockListResponse {
    private Long stockId;
    private String ticker;
    private String name;
    private String market;
    private StockStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
