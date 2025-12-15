package com.team.goldenturtle.ranking.command.application.service;

import com.team.goldenturtle.ranking.command.application.dto.request.RankingNeighborDto;

import java.math.BigDecimal;
import java.util.List;

public interface RankingService {


    List<RankingNeighborDto> getRankingNeighbors(Long sessionId);

    BigDecimal calculateTotalAsset(Long sessionId);

    String getNickname(Long sessionId);
}
