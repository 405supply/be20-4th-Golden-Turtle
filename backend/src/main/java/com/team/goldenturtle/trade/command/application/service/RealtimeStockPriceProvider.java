package com.team.goldenturtle.trade.command.application.service;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RealtimeStockPriceProvider {

    private final Map<String, BigDecimal> priceCache = new ConcurrentHashMap<>();
    private final Map<String, Deque<PriceEntry>> historyCache = new ConcurrentHashMap<>();

    private static final Duration WINDOW = Duration.ofMinutes(20);


    /* FinnhubClientHandler 에서 가격 정보를 받아와서 캐시 업데이트*/
    public void updatePrice(String ticker, BigDecimal price, Long timestamp) {
        priceCache.put(ticker, price);

        historyCache.computeIfAbsent(ticker, k -> new ArrayDeque<>());

        Deque<PriceEntry> deque = historyCache.get(ticker);

        synchronized (deque) {
            // 1. 최신 가격 추가
            deque.addLast(new PriceEntry(timestamp, price));

            // 2. 20분 이전 데이터 제거
            long threshold = timestamp - WINDOW.toMillis();
            while (!deque.isEmpty() && deque.getFirst().timestamp < threshold) {
                deque.removeFirst();
            }
        }
    }

    /* 특정 종목의 현재가 조회 (TradeCommandServiceImpl에서 사용) */
    public Optional<BigDecimal> getPrice(String ticker) {
        return Optional.ofNullable(priceCache.get(ticker));
    }

    public List<PriceEntry> getRecentHistory(String ticker) {
        ticker = "BINANCE:" + ticker.toUpperCase() + "USDT";

        Deque<PriceEntry> deque = historyCache.get(ticker);
        if (deque == null) return List.of();

        synchronized (deque) {
            return new ArrayList<>(deque); // 복사본 반환
        }
    }

    public record PriceEntry(long timestamp, BigDecimal price) {}
}
