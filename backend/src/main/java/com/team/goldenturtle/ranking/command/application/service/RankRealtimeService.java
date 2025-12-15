package com.team.goldenturtle.ranking.command.application.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.team.goldenturtle.ranking.command.application.dto.request.RankingNeighborDto;
import com.team.goldenturtle.websocket.application.service.StompBroadcastService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RankRealtimeService {

    private final RankingService rankingService;
    private final StompBroadcastService stomp;
    private final ObjectMapper objectMapper; // JacksonConfig에서 등록한 Bean 사용

    @Transactional
    public void updateMyRank(Long sessionId) {

        try {
            List<RankingNeighborDto> rankingList = rankingService.getRankingNeighbors(sessionId);
            String destination = "/topic/my-rank/" + sessionId;
            String json = objectMapper.writeValueAsString(rankingList);
            stomp.broadcast(destination, json);

        } catch (JsonProcessingException e) {
            log.error("랭킹 데이터 JSON 변환 실패", e);
        } catch (Exception e) {
            log.error("랭킹 업데이트 중 오류 발생", e);
        }
    }
}