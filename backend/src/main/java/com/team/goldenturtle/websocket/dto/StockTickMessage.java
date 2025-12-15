package com.team.goldenturtle.websocket.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class StockTickMessage {
    private String ticker;
    private Double price;
    private Long timestamp;
    private Double volume;
}
