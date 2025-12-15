package com.team.goldenturtle.ranking.query.controller;

import com.team.goldenturtle.common.dto.ApiResponse;
import com.team.goldenturtle.ranking.query.dto.RankingResponse;
import com.team.goldenturtle.ranking.query.service.RankingService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/ranking")
public class RankingQueryController {

    private final RankingService rankingService;

    @GetMapping
    public ApiResponse<List<RankingResponse>> getRanking(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        List<RankingResponse> rankingList = rankingService.getRanking(page, size);
        return ApiResponse.success(rankingList);
    }
}
