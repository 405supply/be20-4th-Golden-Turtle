package com.team.goldenturtle.stockinfo.command.application.service;

import com.team.goldenturtle.common.enums.StockStatus;
import com.team.goldenturtle.entity.Stock;
import com.team.goldenturtle.stockinfo.command.application.dto.response.FinnhubProfileResponse;
import com.team.goldenturtle.stockinfo.command.infrastructure.repository.StockRepository;
import com.team.goldenturtle.websocket.application.FinnhubClientHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class StockManageServiceImpl implements StockManageService {

    private final StockRepository stockRepository;
    private final FinnhubClientHandler finnhubClientHandler;
    private final FinnhubService finnhubService;

    @Override
    public void addCrypto(String symbol) {
        Stock stock = stockRepository.findByTicker(symbol).orElseThrow(
                () -> new IllegalArgumentException("Invalid symbol"));

        String marketSymbol = stock.getMarket() + ":" + stock.getTicker() + "USDT";

        log.info(marketSymbol);

        stock.setStatus(StockStatus.ACTIVE);
        if (finnhubClientHandler.subscribe(marketSymbol) != FinnhubClientHandler.MessageResult.SUCCESS) {
            throw new IllegalArgumentException("Finnhub websocket 메시지 전송 중 오류 발생");
        }
    }

    @Override
    public void deleteCrypto(String symbol) {
        Stock stock = stockRepository.findByTicker(symbol).orElseThrow(
                () -> new IllegalArgumentException("Invalid symbol")
        );

        String marketSymbol = stock.getMarket() + ":" + stock.getTicker() + "USDT";

        stock.setStatus(StockStatus.INACTIVE);

        if (finnhubClientHandler.unsubscribe(marketSymbol) != FinnhubClientHandler.MessageResult.SUCCESS) {
            throw new IllegalArgumentException("Finnhub websocket 메시지 전송 중 오류 발생");
        }
    }
}
