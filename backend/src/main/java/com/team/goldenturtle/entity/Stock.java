package com.team.goldenturtle.entity;

import com.team.goldenturtle.common.enums.StockStatus;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "tb_stock")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class Stock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "stock_id")
    private Long stockId;

    @Column(nullable = false, length = 20)
    private String ticker;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, length = 50)
    private String market;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StockStatus status;

    @CreatedDate
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Builder
    public Stock(String ticker, String name, String market, StockStatus status) {
        this.ticker = ticker;
        this.name = name;
        this.market = market;
        this.status = status;
    }
    public Long updateData(String name, String market) {
        this.name = name;
        this.market = market;

        return this.stockId;
    }
}
