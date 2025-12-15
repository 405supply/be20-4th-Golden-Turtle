package com.team.goldenturtle.ranking.command.application.service.impl;

import com.team.goldenturtle.entity.GameSession;
import com.team.goldenturtle.entity.Holding;
import com.team.goldenturtle.entity.Ranking;
import com.team.goldenturtle.gamesession.command.infrastructure.repository.GameSessionRepository;
import com.team.goldenturtle.ranking.command.application.dto.request.RankingNeighborDto;
import com.team.goldenturtle.ranking.command.application.service.RankingService;
import com.team.goldenturtle.ranking.command.infrastructure.repository.RankingRepository;
import com.team.goldenturtle.repo.ExHoldingRepository;
import com.team.goldenturtle.trade.command.application.service.RealtimeStockPriceProvider;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RankingServiceImpl implements RankingService {

    private final GameSessionRepository sessionRepository;
    private final ExHoldingRepository holdingRepository;
    private final RealtimeStockPriceProvider priceProvider;
    private final RankingRepository rankingRepository;

    // 실시간 기준 내 주변 랭킹 조회
    @Override
    @Transactional
    public List<RankingNeighborDto> getRankingNeighbors(Long sessionId) {

        GameSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("세션 없음"));

        BigDecimal totalAsset = calculateTotalAsset(sessionId);

        Ranking ranking = rankingRepository.findByGameSession_SessionId(sessionId)
                .orElseGet(() -> createRanking(session, totalAsset));

        ranking.setRankingTotalAsset(totalAsset);

        BigDecimal totalReturn = totalAsset
                .subtract(session.getInitialCash())
                .divide(session.getInitialCash(), 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));

        ranking.setRankingTotalReturn(totalReturn);

        rankingRepository.save(ranking);

        int myRank = rankingRepository.calculateRank(totalAsset, ranking.getRankingId());

        List<Ranking> above = rankingRepository.findAbove(totalAsset, ranking.getRankingId(), PageRequest.of(0, 4));
        List<Ranking> below = rankingRepository.findBelow(totalAsset, ranking.getRankingId(), PageRequest.of(0, 4));

        List<RankingNeighborDto> result = new ArrayList<>();

        Collections.reverse(above);
        int rankCursor = myRank - above.size();
        for (Ranking r : above) {
            result.add(convert(r, rankCursor++, false));
        }

        result.add(convert(ranking, myRank, true));

        rankCursor = myRank + 1;
        for (Ranking r : below) {
            result.add(convert(r, rankCursor++, false));
        }

        return result;
    }

    // 랭킹 엔티티 생성
    private Ranking createRanking(GameSession session, BigDecimal totalAsset) {
        BigDecimal initial = session.getInitialCash();
        BigDecimal totalReturn = totalAsset.subtract(initial)
                .divide(initial, 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));

        Ranking ranking = Ranking.builder()
                .gameSession(session)
                .rankingTotalAsset(totalAsset)
                .rankingTotalReturn(totalReturn)
                .build();

        return rankingRepository.save(ranking);
    }

    // DTO 변환
    private RankingNeighborDto convert(Ranking r, int rank, boolean isMe) {
        return RankingNeighborDto.builder()
                .rank(rank)
                .nickname(r.getGameSession().getUser().getUserNickname())
                .totalAsset(r.getRankingTotalAsset())
                .isMe(isMe)
                .sessionId(r.getGameSession().getSessionId())
                .build();
    }

    // 실시간 자산 계산
    @Override
    public BigDecimal calculateTotalAsset(Long sessionId) {
        GameSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("세션 없음"));

        BigDecimal total = session.getCashBalance();

        List<Holding> holdings = holdingRepository.findByGameSession_SessionId(sessionId);

        for (Holding h : holdings) {
            String ticker = h.getStock().getMarket() + ":" + h.getStock().getTicker() + "USDT";
            BigDecimal price = priceProvider.getPrice(ticker).orElse(BigDecimal.ZERO);
            total = total.add(price.multiply(h.getQuantity()));
        }

        return total;
    }

    // 닉네임 조회
    @Override
    public String getNickname(Long sessionId) {
        return sessionRepository.findById(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("세션 없음"))
                .getUser()
                .getUserNickname();
    }
}
