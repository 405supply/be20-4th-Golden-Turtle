package com.team.goldenturtle.domain.Account.query.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class GameSessionDTO {
    private Long sessionId;
    private Long userId;
    private BigDecimal initialCash;
    private BigDecimal cashBalance;
    private String status;
    private LocalDateTime startedAt;
    private LocalDateTime endedAt;
    private BigDecimal finalAsset;
    private BigDecimal totalReturn;

    @Builder
    public GameSessionDTO(Long sessionId, Long userId, BigDecimal initialCash, BigDecimal cashBalance, String status, LocalDateTime startedAt, LocalDateTime endedAt, BigDecimal finalAsset, BigDecimal totalReturn) {
        this.sessionId = sessionId;
        this.userId = userId;
        this.initialCash = initialCash;
        this.cashBalance = cashBalance;
        this.status = status;
        this.startedAt = startedAt;
        this.endedAt = endedAt;
        this.finalAsset = finalAsset;
        this.totalReturn = totalReturn;
    }
}