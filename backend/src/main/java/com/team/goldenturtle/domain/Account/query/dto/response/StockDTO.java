package com.team.goldenturtle.domain.Account.query.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class StockDTO {
    private Long stockId;
    private String ticker;
    private String name;
    private String market;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
