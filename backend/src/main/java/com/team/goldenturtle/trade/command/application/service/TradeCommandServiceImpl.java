package com.team.goldenturtle.trade.command.application.service;

import com.team.goldenturtle.common.enums.GameSessionStatus;
import com.team.goldenturtle.gamesession.command.infrastructure.repository.GameSessionRepository;
import com.team.goldenturtle.stockinfo.command.infrastructure.repository.StockRepository;
import com.team.goldenturtle.trade.command.application.dto.request.BuyStockCommandRequestDto;
import com.team.goldenturtle.trade.command.application.dto.request.SellStockCommandRequestDto;
import com.team.goldenturtle.trade.command.infrastructure.repository.HoldingRepository;
import com.team.goldenturtle.trade.command.infrastructure.repository.TradeRepository;
import com.team.goldenturtle.entity.*;
import com.team.goldenturtle.common.enums.TradeSide;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class TradeCommandServiceImpl implements TradeCommandService {

    private final GameSessionRepository gameSessionRepository;
    private final StockRepository stockRepository;
    private final HoldingRepository HoldingRepository;
    private final TradeRepository TradeRepository;
    private final RealtimeStockPriceProvider realtimeStockPriceProvider;

    @Override
    @Transactional
    public void handleBuyCommand(Long userId, BuyStockCommandRequestDto command) {
        GameSession gameSession = gameSessionRepository.findByUser_UserIdAndStatus(userId, GameSessionStatus.ACTIVE)
                .orElseThrow(() -> new IllegalStateException("진행중인 게임이 없습니다."));

        User user = gameSession.getUser();

        Stock stock = stockRepository.findByTicker(command.getTicker())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 종목입니다."));

        String fullSymbol = "BINANCE:" + command.getTicker().toUpperCase() + "USDT";
        BigDecimal currentPrice = realtimeStockPriceProvider.getPrice(fullSymbol)
                .orElseThrow(() -> new IllegalStateException("해당 종목의 실시간 가격 정보가 없습니다."));

        BigDecimal quantity = new BigDecimal(command.getQuantity());
        BigDecimal totalPrice = currentPrice.multiply(quantity);

        if (gameSession.getCashBalance().compareTo(totalPrice) < 0) {
            throw new IllegalStateException("잔액이 부족합니다.");
        }
        BigDecimal newBalance = gameSession.getCashBalance().subtract(totalPrice);
        gameSession.setCashBalance(newBalance);

        Holding holding = HoldingRepository.findByGameSessionAndStock(gameSession, stock)
                .orElseGet(() -> {
                    log.info("Step 6a: No existing holding found. Creating a new one.");
                    return Holding.builder()
                            .stock(stock)
                            .gameSession(gameSession)
                            .quantity(BigDecimal.ZERO)
                            .avgPrice(BigDecimal.ZERO)
                            .build();
                });
        if(holding.getHoldingId() != null) {
            log.info("Step 6b: Existing holding found with ID: {}", holding.getHoldingId());
        }


        log.info("Step 7: Preparing to update holding values.");
        BigDecimal oldQuantity = holding.getQuantity() != null ? holding.getQuantity() : BigDecimal.ZERO;
        BigDecimal oldAvgPrice = holding.getAvgPrice() != null ? holding.getAvgPrice() : BigDecimal.ZERO;
        log.info("Step 7a: Old Quantity: {}, Old Avg Price: {}", oldQuantity, oldAvgPrice);

        if (oldQuantity.compareTo(BigDecimal.ZERO) == 0) {
            log.info("Step 8: This is a new purchase of this stock. Setting quantity and avgPrice.");
            holding.setQuantity(quantity);
            holding.setAvgPrice(currentPrice);
        } else {
            log.info("Step 8: This is an additional purchase. Recalculating average price.");
            BigDecimal oldTotalValue = oldAvgPrice.multiply(oldQuantity);
            BigDecimal newTotalValue = currentPrice.multiply(quantity);
            BigDecimal totalQuantity = oldQuantity.add(quantity);
            BigDecimal newAvgPrice = oldTotalValue.add(newTotalValue).divide(totalQuantity, 4, RoundingMode.HALF_UP);
            log.info("Step 8a: Old Value: {}, New Purchase Value: {}, Total Quantity: {}, New Avg Price: {}", oldTotalValue, newTotalValue, totalQuantity, newAvgPrice);

            holding.setQuantity(totalQuantity);
            holding.setAvgPrice(newAvgPrice);
        }
        log.info("Step 8b: New holding values set. Quantity: {}, AvgPrice: {}", holding.getQuantity(), holding.getAvgPrice());


        log.info("Step 9: Building Trade entity.");
        Trade trade = Trade.builder()
                .gameSession(gameSession)
                .user(user)
                .stock(stock)
                .side(TradeSide.BUY)
                .quantity(command.getQuantity())
                .price(currentPrice)
                .amount(totalPrice)
                .build();
        log.info("Step 9a: Trade entity built.");

        log.info("Step 10: Saving all entities to the database.");
        gameSessionRepository.save(gameSession);
        HoldingRepository.save(holding);
        TradeRepository.save(trade);
        log.info("--- handleBuyCommand successfully completed.");
    }
    
    @Override
    @Transactional
    public void handleSellCommand(Long userId, SellStockCommandRequestDto command) {
        GameSession gameSession = gameSessionRepository.findByUser_UserIdAndStatus(userId, GameSessionStatus.ACTIVE).orElseThrow(() -> new IllegalStateException("진행중인 게임이 없습니다."));
        User user = gameSession.getUser();
        Stock stock = stockRepository.findByTicker(command.getTicker()).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 종목입니다."));

        Holding holding = HoldingRepository.findByGameSessionAndStock(gameSession, stock)
                .orElseThrow(() -> new IllegalStateException("보유하고 있지 않은 주식입니다."));

        BigDecimal quantityToSell = new BigDecimal(command.getQuantity());
        BigDecimal currentQuantity = holding.getQuantity() != null ? holding.getQuantity() : BigDecimal.ZERO;

        if (currentQuantity.compareTo(quantityToSell) < 0) {
            throw new IllegalStateException("보유 수량보다 많이 매도할 수 없습니다.");
        }
        holding.setQuantity(currentQuantity.subtract(quantityToSell));
        if (holding.getQuantity().compareTo(BigDecimal.ZERO) == 0) {
            holding.setAvgPrice(BigDecimal.ZERO);
        }

        String fullSymbol = "BINANCE:" + command.getTicker().toUpperCase() + "USDT";
        BigDecimal currentPrice = realtimeStockPriceProvider.getPrice(fullSymbol)
                .orElseThrow(() -> new IllegalStateException("해당 종목의 실시간 가격 정보가 없습니다."));
        BigDecimal totalSalePrice = currentPrice.multiply(quantityToSell);
        gameSession.setCashBalance(gameSession.getCashBalance().add(totalSalePrice));
        
        Trade trade = Trade.builder()
                .gameSession(gameSession)
                .user(user)
                .stock(stock)
                .side(TradeSide.SELL)
                .quantity(command.getQuantity())
                .price(currentPrice)
                .amount(totalSalePrice)
                .build();

        gameSessionRepository.save(gameSession);
        HoldingRepository.save(holding);
        TradeRepository.save(trade);
    }
}
