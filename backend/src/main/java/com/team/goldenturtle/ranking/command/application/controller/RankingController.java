package com.team.goldenturtle.ranking.command.application.controller;

import com.team.goldenturtle.common.dto.ApiResponse;
import com.team.goldenturtle.ranking.command.application.dto.request.RankingNeighborDto;
import com.team.goldenturtle.ranking.command.application.service.RankingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/ranking")
public class RankingController {

    private final RankingService rankingService;

    @GetMapping("/my/{sessionId}")
    public ResponseEntity<ApiResponse<?>> getMyRank(@PathVariable Long sessionId) {

        List<RankingNeighborDto> neighbors = rankingService.getRankingNeighbors(sessionId);

        RankingNeighborDto me = neighbors.stream()
                .filter(RankingNeighborDto::isMe)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("랭킹 정보 없음"));

        return ResponseEntity.ok(
                ApiResponse.success(
                        Map.of(
                                "rank", me.getRank(),
                                "nickname", me.getNickname(),
                                "totalAsset", me.getTotalAsset()
                        )
                )
        );
    }
}
