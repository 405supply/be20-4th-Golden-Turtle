package com.team.goldenturtle.stockinfo.command.infrastructure.repository;

import com.team.goldenturtle.entity.Quote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QuoteRepository extends JpaRepository<Quote, Long> {
    Optional<Quote> findByTicker(String ticker);
    List<Quote> findAll();
}
