package com.team.goldenturtle.domain.Account.query.dto.response;

import com.team.goldenturtle.common.enums.UserStatus;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class TradeDTO {
    private Long tradeId;
    private GameSessionDTO gameSession;
    private UserDTO User;
    private StockDTO stock;
    private Integer quantity;
    private BigDecimal price;
    private BigDecimal amount;
    private String side;
    private LocalDateTime createdAt;
}
