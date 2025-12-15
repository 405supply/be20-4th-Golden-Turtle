package com.team.goldenturtle.websocket.application;

import com.team.goldenturtle.stockinfo.query.dto.response.StockListResponse;
import com.team.goldenturtle.stockinfo.query.service.StockInfoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.client.WebSocketClient;

import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
@Order(2)
public class FinnhubWebsocketRunner implements CommandLineRunner {

    private final WebSocketClient webSocketClient;
    private final FinnhubClientHandler finnhubClientHandler;
    private final StockInfoService  stockInfoService;

    @Value("${websocket.finnhub.url}")
    private String finnhubUrl;
    @Value("${finnhub.api.key}")
    private String finnhubApiKey;

    @Override
    public void run(String... args) throws Exception {
        log.info("Finnhub Websocket 연결 시도 중...");

        String url = String.format("%s?token=%s", finnhubUrl, finnhubApiKey);

        webSocketClient.execute(finnhubClientHandler, url);

        Thread.sleep(5000);

        // Active 종목들 websocket에 구독 요청
        List<StockListResponse> activeStocks = stockInfoService.getActiveStocks();

        activeStocks.forEach(stock ->
                finnhubClientHandler.subscribe(stock.getMarket() + ":" + stock.getTicker() + "USDT")
        );
    }
}
