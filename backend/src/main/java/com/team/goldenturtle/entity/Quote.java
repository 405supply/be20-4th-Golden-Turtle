package com.team.goldenturtle.entity;

import com.team.goldenturtle.stockinfo.query.dto.response.StockMetaResponse;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;

@Entity
@Table(name = "tb_quote")
@Getter
@NoArgsConstructor
public class Quote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "quote_id")
    private Long quoteId;

    @Column(name = "ticker", nullable = false)
    private String ticker;

    @Column(name = "high_price", precision = 18, scale = 4)
    private BigDecimal highPrice;

    @Column(name = "low_price", precision = 18, scale = 4)
    private BigDecimal lowPrice;

    @Column(name = "open_price", precision = 18, scale = 4)
    private BigDecimal openPrice;

    @Column(name = "close_price", precision = 18, scale = 4)
    private BigDecimal closePrice;

    @Column(name = "percent_change")
    private Double percentChange;

    @Column(name = "timestamp", nullable = false)
    private Long timestamp;

    @Builder
    public Quote(
            String ticker,
            BigDecimal highPrice,
            BigDecimal lowPrice,
            BigDecimal openPrice,
            BigDecimal closePrice,
            Double percentChange,
            Long timestamp) {
        this.ticker = ticker;
        this.highPrice = highPrice;
        this.lowPrice = lowPrice;
        this.openPrice = openPrice;
        this.closePrice = closePrice;
        this.percentChange = percentChange;
        this.timestamp = timestamp;
    }

    public Long updateQuote(StockMetaResponse quote) {
        this.highPrice = quote.getHighPrice() != null ? quote.getHighPrice() : this.highPrice;
        this.lowPrice = quote.getLowPrice() != null ? quote.getLowPrice() : this.lowPrice;
        this.openPrice = quote.getOpenPrice() != null ? quote.getOpenPrice() : this.openPrice;
        this.closePrice = quote.getClosePrice() != null ? quote.getClosePrice() : this.closePrice;
        this.percentChange = quote.getPercentChange() != null ? quote.getPercentChange() : this.percentChange;
        this.timestamp = quote.getTimestamp();

        return this.quoteId;
    }

    public boolean isExpired() {
        return
                this.timestamp != null &&
                Duration.between(Instant.ofEpochSecond(this.timestamp), Instant.now()).compareTo(Duration.ofDays(1)) >= 0;
    }
}
