package com.team.goldenturtle.stockinfo.command.infrastructure.repository;

import com.team.goldenturtle.entity.Stock;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StockRepository extends JpaRepository<Stock, Long> {
    Optional<Stock> findByTicker(String ticker);
}
