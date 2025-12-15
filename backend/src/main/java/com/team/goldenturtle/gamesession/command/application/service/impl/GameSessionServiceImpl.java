package com.team.goldenturtle.gamesession.command.application.service.impl;

import com.team.goldenturtle.common.enums.GameSessionStatus;
import com.team.goldenturtle.common.enums.TradeSide;
import com.team.goldenturtle.entity.*;
import com.team.goldenturtle.gamesession.command.application.dto.response.EndGameSessionResponse;
import com.team.goldenturtle.gamesession.command.application.service.GameSessionService;
import com.team.goldenturtle.gamesession.command.infrastructure.repository.GameSessionRepository;
import com.team.goldenturtle.ranking.command.application.dto.request.RankingNeighborDto;
import com.team.goldenturtle.ranking.command.application.service.RankingService;
import com.team.goldenturtle.ranking.command.infrastructure.repository.RankingRepository;
import com.team.goldenturtle.repo.ExHoldingRepository;
import com.team.goldenturtle.repo.ExTradeRepository;
import com.team.goldenturtle.trade.command.application.service.RealtimeStockPriceProvider;
import com.team.goldenturtle.user.command.infrastructure.repository.UserRepository;
import com.team.goldenturtle.websocket.application.FinnhubClientHandler;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Service
@Transactional
public class GameSessionServiceImpl implements GameSessionService {
    private final GameSessionRepository gameSessionRepository;
    private final UserRepository userRepository;
    private final ExHoldingRepository exHoldingRepository;
    private final FinnhubClientHandler finnhubClientHandler;
    private final ExTradeRepository exTradeRepository;
    private final RankingService rankingService;
    private final RedisTemplate<String, String> redisTemplate;
    private final RealtimeStockPriceProvider provider;
    @Override
    public Long startGameSession(Long userId) {
        gameSessionRepository.findByUser_UserIdAndStatus(userId, GameSessionStatus.ACTIVE)
                .ifPresent(s -> {
                    throw new IllegalStateException("이미 진행 중인 게임이 있습니다. 종료 후 다시 시작하세요.");
                });
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유저 없음"));

        GameSession session = GameSession.builder()
                .user(user)
                .initialCash(BigDecimal.valueOf(10000))
                .cashBalance(BigDecimal.valueOf(10000))
                .status(GameSessionStatus.ACTIVE)
                .startedAt(LocalDateTime.now())
                .build();
        gameSessionRepository.save(session);
        finnhubClientHandler.registerSession(session.getSessionId());
        String key = "GAME_SESSION:" + session.getSessionId();
        redisTemplate.opsForValue().set(key, "ACTIVE", Duration.ofMinutes(10));

        return session.getSessionId();
    }

    @Override
    @Transactional
    public EndGameSessionResponse endGameSession(Long userId) {

        GameSession session = gameSessionRepository.findByUser_UserIdAndStatus(userId, GameSessionStatus.ACTIVE)
                .orElseThrow(() -> new IllegalArgumentException("게임 없음"));

        BigDecimal updatedCashBalance = session.getCashBalance();

        List<Holding> holdings = exHoldingRepository.findByGameSession(session);

        for (Holding holding : holdings) {

            Stock stock = holding.getStock();
            String ticker = stock.getMarket() + ":" + stock.getTicker() + "USDT";
            BigDecimal quantity = holding.getQuantity();

            BigDecimal sellPrice = provider.getPrice(ticker)
                    .orElseThrow(() -> new IllegalStateException("종목 없음"));

            BigDecimal sellAmount = sellPrice.multiply(quantity);

            Trade trade = Trade.builder()
                    .gameSession(session)
                    .user(session.getUser())
                    .stock(stock)
                    .side(TradeSide.SELL)
                    .quantity(quantity.intValue())
                    .price(sellPrice)
                    .amount(sellAmount)
                    .createdAt(LocalDateTime.now())
                    .build();

            exTradeRepository.save(trade);

            updatedCashBalance = updatedCashBalance.add(sellAmount);
        }

        exHoldingRepository.deleteAll(holdings);

        session.updatedCashBalance(updatedCashBalance);
        session.updatedFinalAsset(updatedCashBalance);

        BigDecimal totalReturn = updatedCashBalance.subtract(session.getInitialCash())
                .divide(session.getInitialCash(), 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));

        session.updatedTotalReturn(totalReturn);
        session.updatedEndedAt(LocalDateTime.now());
        session.updatedStatus(GameSessionStatus.CLOSED);

        finnhubClientHandler.unregisterSession(session.getSessionId());
        redisTemplate.delete("GAME_SESSION:" + session.getSessionId());

        List<RankingNeighborDto> neighbors = rankingService.getRankingNeighbors(session.getSessionId());

        int myRank = neighbors.stream()
                .filter(RankingNeighborDto::isMe)
                .findFirst()
                .map(RankingNeighborDto::getRank)
                .orElse(9999); // 안전장치

        return new EndGameSessionResponse(
                myRank,
                session.getUser().getUserNickname(),
                session.getFinalAsset(),
                session.getTotalReturn()
        );
    }


    @Override
    public void forceEndBySessionId(Long sessionId) {
        GameSession session = gameSessionRepository.findById(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("세션 없음"));

        if (session.getStatus() == GameSessionStatus.CLOSED) {
            redisTemplate.delete("GAME_SESSION:" + sessionId);
            return;
        }

        Long userId = session.getUser().getUserId();
        System.out.println("강제 종료 실행: session=" + sessionId);
        endGameSession(userId);
    }


}
