package com.team.goldenturtle.gamesession.command.infrastructure.repository;

import com.team.goldenturtle.common.enums.GameSessionStatus;
import com.team.goldenturtle.entity.GameSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.support.SessionStatus;

import java.util.Optional;

public interface GameSessionRepository extends JpaRepository<GameSession,Long> {
    Optional<GameSession> findByUser_UserIdAndStatus(Long userId, GameSessionStatus gameSessionStatus);

}

