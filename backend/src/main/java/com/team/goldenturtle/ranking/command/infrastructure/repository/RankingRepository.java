package com.team.goldenturtle.ranking.command.infrastructure.repository;

import com.team.goldenturtle.entity.Ranking;
import com.team.goldenturtle.ranking.query.projection.RankingResponseProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface RankingRepository extends JpaRepository<Ranking, Long> {

    Optional<Ranking> findByGameSession_SessionId(Long sessionId);

    // 내 등수 계산 (전역)
    @Query("""
                SELECT COUNT(r) + 1
                FROM Ranking r
                WHERE r.rankingTotalAsset > :asset
                   OR (r.rankingTotalAsset = :asset AND r.rankingId < :myId)
            """)
    int calculateRank(@Param("asset") BigDecimal asset, @Param("myId") Long myId);

    @Query(value = """
                SELECT 
                    r.rankingId AS rankingId,
                    gs.sessionId AS sessionId,
                    u.userId AS userId,
                    u.userNickname AS nickname,
                    r.rankingTotalAsset AS totalAsset,
                    r.rankingTotalReturn AS totalReturn,
                    (SELECT COUNT(r2) + 1
                     FROM Ranking r2
                     WHERE r2.rankingTotalAsset > r.rankingTotalAsset
                        OR (r2.rankingTotalAsset = r.rankingTotalAsset AND r2.rankingId < r.rankingId)
                    ) AS rank
                 FROM Ranking r
                 JOIN r.gameSession gs
                 JOIN gs.user u
                 ORDER BY r.rankingTotalAsset DESC, r.rankingId ASC
            """,
            countQuery = """
                         SELECT COUNT(r) FROM Ranking r
                    """
    )
    Page<RankingResponseProjection> findRankingPage(Pageable pageable);

    // 내 위 2명
    @Query("""
                SELECT r FROM Ranking r
                JOIN FETCH r.gameSession gs
                JOIN FETCH gs.user
                WHERE r.rankingTotalAsset > :asset
                   OR (r.rankingTotalAsset = :asset AND r.rankingId < :myId)
                ORDER BY r.rankingTotalAsset ASC, r.rankingId DESC
            """)
    List<Ranking> findAbove(
            @Param("asset") BigDecimal asset,
            @Param("myId") Long myId,
            Pageable pageable);

    // 내 아래 2명
    @Query("""
                SELECT r FROM Ranking r
                JOIN FETCH r.gameSession gs
                JOIN FETCH gs.user
                WHERE r.rankingTotalAsset < :asset
                   OR (r.rankingTotalAsset = :asset AND r.rankingId > :myId)
                ORDER BY r.rankingTotalAsset DESC, r.rankingId ASC
            """)
    List<Ranking> findBelow(
            @Param("asset") BigDecimal asset,
            @Param("myId") Long myId,
            Pageable pageable);

}
