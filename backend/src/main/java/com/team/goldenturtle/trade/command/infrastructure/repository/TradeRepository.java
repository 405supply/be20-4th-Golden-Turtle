package com.team.goldenturtle.trade.command.infrastructure.repository;

import com.team.goldenturtle.entity.Trade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface TradeRepository extends JpaRepository<Trade, Long> {

}
