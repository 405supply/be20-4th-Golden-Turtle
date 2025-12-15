package com.team.goldenturtle.stockinfo.command.application;

import com.team.goldenturtle.stockinfo.command.application.service.FinnhubService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
@Order(1)
public class CryptoStockRunner implements CommandLineRunner {

    private final FinnhubService finnhubService;
    @Override
    public void run(String... args) throws Exception {
        finnhubService.fetchCryptoProfiles();
        log.info("Finnhub crypto 목록 fetch됨.");
    }
}
