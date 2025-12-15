package com.team.goldenturtle.ranking.query.projection;

import java.math.BigDecimal;

public interface RankingResponseProjection {
    Long getRankingId();
    Long getSessionId();
    Long getUserId();
    String getNickname();
    BigDecimal getTotalAsset();
    BigDecimal getTotalReturn();
    Integer getRank();
}
