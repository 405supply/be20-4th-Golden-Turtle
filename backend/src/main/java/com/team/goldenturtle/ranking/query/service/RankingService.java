package com.team.goldenturtle.ranking.query.service;

import com.team.goldenturtle.ranking.command.infrastructure.repository.RankingRepository;
import com.team.goldenturtle.ranking.query.dto.RankingResponse;
import com.team.goldenturtle.ranking.query.projection.RankingResponseProjection;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RankingService {

    private final RankingRepository rankingRepository;

    public List<RankingResponse> getRanking(int page, int size) {

        Pageable pageable = PageRequest.of(page - 1, size);

        Page<RankingResponseProjection> result =
                rankingRepository.findRankingPage(pageable);

        return result.getContent().stream()
                .map(r -> {
                    RankingResponse dto = new RankingResponse();
                    dto.setRankingId(r.getRankingId());
                    dto.setSessionId(r.getSessionId());
                    dto.setUserId(r.getUserId());
                    dto.setNickname(r.getNickname());
                    dto.setTotalAsset(r.getTotalAsset());
                    dto.setTotalReturn(r.getTotalReturn());
                    dto.setRank(r.getRank());
                    return dto;
                })
                .toList();
    }
}
