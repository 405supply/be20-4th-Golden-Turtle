package com.team.goldenturtle.repo;

import com.team.goldenturtle.entity.GameSession;
import com.team.goldenturtle.entity.Holding;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface ExHoldingRepository extends JpaRepository<Holding, Long> {
    List<Holding> findByGameSession(GameSession gameSession);

    List<Holding> findByGameSession_SessionId(Long sessionId);
}