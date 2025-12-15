package com.team.goldenturtle.gamesession.command.application.dto.response;

import lombok.Getter;

import java.math.BigDecimal;
@Getter
public class EndGameSessionResponse {
    private int rank;
    private String nickname;
    private BigDecimal finalAsset;
    private BigDecimal totalReturn;

    public EndGameSessionResponse(int rank, String nickname, BigDecimal finalAsset, BigDecimal totalReturn) {
        this.rank = rank;
        this.nickname = nickname;
        this.finalAsset = finalAsset;
        this.totalReturn = totalReturn;
    }
}
