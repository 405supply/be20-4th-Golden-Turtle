package com.team.goldenturtle.trade.command.infrastructure.repository;

import com.team.goldenturtle.entity.GameSession;
import com.team.goldenturtle.entity.Holding;
import com.team.goldenturtle.entity.Stock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface HoldingRepository extends JpaRepository<Holding, Long> {
    Optional<Holding> findByGameSessionAndStock(GameSession gameSession, Stock stock);
}
