package com.team.goldenturtle.gamesession.command.application.service.listener;

import com.team.goldenturtle.gamesession.command.application.service.GameSessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SessionExpireListener implements MessageListener {

    private final GameSessionService gameSessionService;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        String expiredKey = message.toString();

        if (expiredKey.startsWith("GAME_SESSION:")) {
            Long sessionId = Long.parseLong(expiredKey.split(":")[1]);
            System.out.println("Redis TTL EXPIRED → 강제 종료: " + sessionId);

            gameSessionService.forceEndBySessionId(sessionId);
        }
    }
}
