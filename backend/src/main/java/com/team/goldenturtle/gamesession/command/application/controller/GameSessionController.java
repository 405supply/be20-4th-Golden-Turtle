package com.team.goldenturtle.gamesession.command.application.controller;

import com.sun.security.auth.UserPrincipal;
import com.team.goldenturtle.auth.command.application.service.CustomUserDetails;
import com.team.goldenturtle.common.dto.ApiResponse;
import com.team.goldenturtle.gamesession.command.application.dto.response.EndGameSessionResponse;
import com.team.goldenturtle.gamesession.command.application.service.GameSessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/game-session")
public class GameSessionController {
    private final GameSessionService gameSessionService;
    @PostMapping("/start")
    public ResponseEntity<ApiResponse<?>> startGame(@AuthenticationPrincipal CustomUserDetails userDetails) {
        Long sessionId = gameSessionService.startGameSession(userDetails.getUserId());
        return ResponseEntity.ok(ApiResponse.success(sessionId));
    }

    @PostMapping("/end")
    public ResponseEntity<ApiResponse<?>> endGameSession(@AuthenticationPrincipal CustomUserDetails userDetails){
        EndGameSessionResponse endGameSessionResponse = gameSessionService.endGameSession(userDetails.getUserId());
        return ResponseEntity.ok(ApiResponse.success(endGameSessionResponse));
    }
}
