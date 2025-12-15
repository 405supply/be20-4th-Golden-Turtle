package com.team.goldenturtle.stockinfo.query.service;

import com.team.goldenturtle.common.exception.BusinessException;
import com.team.goldenturtle.common.exception.ErrorCode;
import com.team.goldenturtle.domain.Account.query.dto.response.StockDetailResponse;
import com.team.goldenturtle.entity.Quote;
import com.team.goldenturtle.stockinfo.command.application.dto.response.FinnhubCryptoResponse;
import com.team.goldenturtle.stockinfo.command.infrastructure.repository.QuoteRepository;
import com.team.goldenturtle.stockinfo.command.infrastructure.repository.StockRepository;
import com.team.goldenturtle.stockinfo.query.dto.response.StockDailyPercentageResponse;
import com.team.goldenturtle.stockinfo.query.dto.response.StockListResponse;
import com.team.goldenturtle.stockinfo.query.dto.response.StockMetaResponse;
import com.team.goldenturtle.stockinfo.query.mapper.StockMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class StockInfoServiceImpl implements StockInfoService {

    private final StockMapper stockMapper;
    private final QuoteRepository quoteRepository;
    private final WebClient finnhubClient;

    @Value("${finnhub.api.key}")
    private String finnhubApiKey;

    @Override
    public List<StockListResponse> getAllStocks(int page, int size) {
        int offset = (page - 1) * size;

        return stockMapper.findAllStocks(size, offset);
    }

    @Override
    public List<StockListResponse> getActiveStocks() {
        return stockMapper.findActiveStocks();
    }


    @Override
    public StockMetaResponse getStockMeta(String ticker) {
        Quote quote = quoteRepository.findByTicker(ticker).orElse(null);



        if (!Objects.isNull(quote) && !quote.isExpired()) {
            return StockMetaResponse.builder()
                    .highPrice(quote.getHighPrice())
                    .lowPrice(quote.getLowPrice())
                    .openPrice(quote.getOpenPrice())
                    .closePrice(quote.getClosePrice())
                    .percentChange(quote.getPercentChange())
                    .timestamp(quote.getTimestamp())
                    .build();
        }

        else {
            String symbol = "BINANCE" + ":" + ticker.toUpperCase() + "USDT";

            StockMetaResponse meta = finnhubClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/quote")
                            .queryParam("symbol", symbol)
                            .queryParam("token", finnhubApiKey)
                            .build())
                    .retrieve()
                    .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
                            ClientResponse::createException)
                    .bodyToMono(StockMetaResponse.class)
                    .block();

            if (!Objects.nonNull(meta)) {
                throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND);
            }

            if (Objects.isNull(quote)) {
                quote = Quote.builder()
                        .ticker(ticker)
                        .highPrice(meta.getHighPrice())
                        .lowPrice(meta.getLowPrice())
                        .openPrice(meta.getOpenPrice())
                        .closePrice(meta.getClosePrice())
                        .percentChange(meta.getPercentChange())
                        .timestamp(meta.getTimestamp())
                        .build();

                quoteRepository.save(quote);
            }

            else {
                quote.updateQuote(meta);
            }


            return meta;
        }
    }

    @Override
    public List<StockDailyPercentageResponse> getStocksDailyPercentage() {
        List<Quote> quotes = quoteRepository.findAll();

        return quotes.stream().map(
                quote -> StockDailyPercentageResponse.builder()
                        .ticker(quote.getTicker())
                        .dp(quote.getPercentChange())
                        .timestamp(quote.getTimestamp())
                        .build())
                .toList();
    }
}
