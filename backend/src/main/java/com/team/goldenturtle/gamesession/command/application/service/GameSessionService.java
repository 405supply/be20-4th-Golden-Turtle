package com.team.goldenturtle.gamesession.command.application.service;

import com.team.goldenturtle.gamesession.command.application.dto.response.EndGameSessionResponse;
import org.springframework.stereotype.Service;

@Service
public interface GameSessionService {

    //시작 기능 추가 (가상 자금 지급)
    public Long startGameSession(Long userId);

    //종료 기능 추가(보유한 종목은 시장가로 전량 매도 및 초기화 랭킹 반영)
    public EndGameSessionResponse endGameSession(Long userId);
    //시간 지나면 강제 종료
    void forceEndBySessionId(Long sessionId);
}
