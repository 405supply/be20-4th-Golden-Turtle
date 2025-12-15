package com.team.goldenturtle.ranking.query.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class RankingResponse {
    private Long rankingId;
    private Long sessionId;
    private Long userId;
    private String nickname;
    private BigDecimal totalAsset;
    private BigDecimal totalReturn;
    private Integer rank;
}