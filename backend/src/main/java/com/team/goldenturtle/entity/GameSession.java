package com.team.goldenturtle.entity;

import com.team.goldenturtle.common.enums.GameSessionStatus;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "tb_game_session")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GameSession {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "session_id")
    private Long sessionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "initial_cash", nullable = false, precision = 18, scale = 2)
    private BigDecimal initialCash = BigDecimal.valueOf(10000.00);

    @Column(name = "cash_balance", nullable = false, precision = 18, scale = 2)
    private BigDecimal cashBalance = BigDecimal.valueOf(10000.00);

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private GameSessionStatus status = GameSessionStatus.ACTIVE;

    @CreatedDate
    @Column(name = "started_at", nullable = false)
    private LocalDateTime startedAt;

    @Column(name = "ended_at", nullable = true)
    private LocalDateTime endedAt;

    @Column(name = "final_asset", precision = 18, scale = 2)
    private BigDecimal finalAsset;

    @Column(name = "total_return", precision = 10, scale = 4)
    private BigDecimal totalReturn;

    @Builder
    public GameSession(User user, BigDecimal initialCash, BigDecimal cashBalance, GameSessionStatus status, LocalDateTime startedAt, LocalDateTime endedAt, BigDecimal finalAsset, BigDecimal totalReturn) {
        this.user = user;
        this.initialCash = initialCash;
        this.cashBalance = cashBalance;
        this.status = status;
        this.startedAt = startedAt;
        this.endedAt = endedAt;
        this.finalAsset = finalAsset;
        this.totalReturn = totalReturn;
    }

    public void updatedTotalReturn(BigDecimal totalReturn) {
        this.totalReturn = totalReturn;
    }

    public void updatedEndedAt(LocalDateTime endedAt) {
        this.endedAt = endedAt;
    }

    public void updatedStatus(GameSessionStatus status) {
        this.status = status;
    }
    public void updatedCashBalance(BigDecimal cashBalance) {
        this.cashBalance = cashBalance;
    }
    public void updatedFinalAsset(BigDecimal finalAsset) {
        this.finalAsset = finalAsset;
    }


}
