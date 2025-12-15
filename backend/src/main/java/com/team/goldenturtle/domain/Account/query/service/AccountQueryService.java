package com.team.goldenturtle.domain.Account.query.service;

import com.team.goldenturtle.common.dto.Pagination;
import com.team.goldenturtle.domain.Account.query.dto.request.AccountSearchRequest;
import com.team.goldenturtle.domain.Account.query.dto.response.GameSessionDTO;
import com.team.goldenturtle.domain.Account.query.dto.response.GameSessionListResponse;
import com.team.goldenturtle.domain.Account.query.dto.response.HoldingDTO;
import com.team.goldenturtle.domain.Account.query.dto.response.HoldingListResponse;
import com.team.goldenturtle.domain.Account.query.dto.response.TradeDTO;
import com.team.goldenturtle.domain.Account.query.dto.response.TradeListResponse;
import com.team.goldenturtle.domain.Account.query.mapper.AccountMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AccountQueryService {

    private final AccountMapper accountMapper;

    @Transactional
    public HoldingListResponse getHoldings(AccountSearchRequest accountSearchRequest) {
        List<HoldingDTO> holdings = accountMapper.findHoldingsByUserId(accountSearchRequest);

        // 총매수액 계산: avgPrice * quantity
        for (HoldingDTO holding : holdings) {
            BigDecimal quantity = holding.getQuantity() != null
                    ? holding.getQuantity()
                    : BigDecimal.ZERO;

            BigDecimal avgPrice = holding.getAvgPrice() != null
                    ? holding.getAvgPrice()
                    : BigDecimal.ZERO;

            BigDecimal totalAmount = avgPrice.multiply(quantity);

            holding.setTotalAmount(totalAmount);
        }

        long totalItems = accountMapper.countHoldings(accountSearchRequest);

        int page = accountSearchRequest.getPage();
        int size = accountSearchRequest.getSize();

        return HoldingListResponse.builder()
                .holdings(holdings)
                .pagination(Pagination.builder()
                        .currentPage(page)
                        .totalPages((int) Math.ceil((double) totalItems / size))
                        .totalItems(totalItems)
                        .build())
                .build();
    }

    @Transactional
    public TradeListResponse getTrades(AccountSearchRequest accountSearchRequest) {
        List<TradeDTO> trades = accountMapper.findTradesBySessionId(accountSearchRequest);

        long totalItems = accountMapper.countTrades(accountSearchRequest);

        int page = accountSearchRequest.getPage();
        int size = accountSearchRequest.getSize();

        return TradeListResponse.builder()
                .trades(trades)
                .pagination(Pagination.builder()
                        .currentPage(page)
                        .totalPages((int) Math.ceil((double) totalItems / size))
                        .totalItems(totalItems)
                        .build())
                .build();
    }

    @Transactional
    public BigDecimal getBalance(Long userId) {
        BigDecimal balance = accountMapper.selectCashBalanceByUserId(userId);
        return balance;
    }

    @Transactional
    public GameSessionListResponse getGameSessions(AccountSearchRequest accountSearchRequest) {
        List<GameSessionDTO> gameSessions = accountMapper.findGameSessionByUserId(accountSearchRequest);
        long totalItems = accountMapper.countGameSession(accountSearchRequest);

        int page = accountSearchRequest.getPage();
        int size = accountSearchRequest.getSize();

        return GameSessionListResponse.builder()
                .gameSessions(gameSessions)
                .pagination(Pagination.builder()
                        .currentPage(page)
                        .totalPages((int) Math.ceil((double) totalItems / size))
                        .totalItems(totalItems)
                        .build())
                .build();
    }
}
