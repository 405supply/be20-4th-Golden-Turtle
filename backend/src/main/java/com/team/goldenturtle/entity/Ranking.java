package com.team.goldenturtle.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "tb_ranking")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Ranking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // AUTO_INCREMENT가 아니면 조정
    @Column(name = "ranking_id")
    private Long rankingId;

    // 🔹 외래키 매핑: session_id -> GameSession
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id", nullable = false)
    private GameSession gameSession;

    @Column(name = "ranking_total_asset", nullable = false, precision = 18, scale = 2)
    private BigDecimal rankingTotalAsset;

    @Column(name = "ranking_total_return", nullable = false, precision = 10, scale = 4)
    private BigDecimal rankingTotalReturn;

    @Builder
    public Ranking(GameSession gameSession, BigDecimal rankingTotalAsset, BigDecimal rankingTotalReturn) {
        this.gameSession = gameSession;
        this.rankingTotalAsset = rankingTotalAsset;
        this.rankingTotalReturn = rankingTotalReturn;

    }
}
