package com.team.goldenturtle.domain.Account.query.dto.response;

import com.team.goldenturtle.entity.GameSession;
import com.team.goldenturtle.entity.Stock;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class HoldingDTO {
    private Long holdingId;
    private StockDTO stock;
    private GameSessionDTO gameSession;
    private BigDecimal quantity;
    private BigDecimal totalAmount;
    private BigDecimal avgPrice;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
