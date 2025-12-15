package com.team.goldenturtle.ranking.command.application.dto.request;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class RankingNeighborDto {
    private int rank;
    private String nickname;
    private BigDecimal totalAsset;
    private boolean isMe;
    private Long sessionId;
}