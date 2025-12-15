package com.team.goldenturtle.stockinfo.command.application.service;

import com.team.goldenturtle.common.enums.StockStatus;
import com.team.goldenturtle.entity.Stock;
import com.team.goldenturtle.stockinfo.command.application.dto.response.FinnhubCryptoResponse;
import com.team.goldenturtle.stockinfo.command.application.dto.response.FinnhubProfileResponse;
import com.team.goldenturtle.stockinfo.command.infrastructure.repository.StockRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class FinnhubServiceImpl implements FinnhubService {

    private final WebClient finnhubClient;
    private final StockRepository stockRepository;

    @Value("${finnhub.api.key}")
    private String finnhubApiKey;

    @Override
    public FinnhubProfileResponse getProfile(String symbol) {
        return finnhubClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/stock/profile2")
                        .queryParam("symbol", symbol)
                        .queryParam("token", finnhubApiKey)
                        .build())
                .retrieve()
                .onStatus(
                        status -> status.is4xxClientError() || status.is5xxServerError(),
                        clientResponse -> clientResponse
                                .bodyToMono(String.class)
                                .flatMap(body -> {
                                    log.error("Finnhub API Error: status={}, body={}",
                                            clientResponse.statusCode(), body);
                                    return Mono.error(new RuntimeException(
                                            "Finnhub API error: " + clientResponse.statusCode()));
                                })
                )
                .bodyToMono(FinnhubProfileResponse.class)
                .doOnError(e -> log.error("Finnhub API 호출 중 오류 발생 " + e))
                .block();
    }

    @Override
    public void fetchCryptoProfiles() {
        FinnhubCryptoResponse[] symbols = finnhubClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/crypto/symbol")
                        .queryParam("exchange", "BINANCE")
                        .queryParam("token", finnhubApiKey)
                        .build())
                .retrieve()
                .bodyToMono(FinnhubCryptoResponse[].class)
                .block();

        if (symbols == null) {
            return;
        }

        for (FinnhubCryptoResponse s : symbols) {
            String ticker = extractBaseCurrency(s.getDisplaySymbol());
            String market = extractExchange(s.getSymbol());

            if (s.getDisplaySymbol() == null || !s.getDisplaySymbol().endsWith("/USDT")) {
                continue;
            }

            Stock stock = stockRepository.findByTicker(ticker).orElse(null);

            if (Objects.isNull(stock)) {
                log.info("새로운 종목 발견, db에 추가 중 : " + ticker);

                stock = Stock.builder()
                    .ticker(ticker)
                    .name(s.getDescription())
                    .market(market)
                    .status(StockStatus.INACTIVE)
                    .build();
                stockRepository.save(stock);
            }
        }
    }

    private String extractExchange(String symbol) {
        if (symbol == null) return null;
        int idx = symbol.indexOf(':');
        return idx > 0 ? symbol.substring(0, idx) : null;
    }

    private String extractBaseCurrency(String displaySymbol) {
        if (displaySymbol == null) return null;
        String[] parts = displaySymbol.split("/");
        return parts.length > 0 ? parts[0] : null;
    }
}
