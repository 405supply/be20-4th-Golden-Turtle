package com.team.goldenturtle.domain.Account.query.mapper;


import com.team.goldenturtle.domain.Account.query.dto.request.AccountSearchRequest;
import com.team.goldenturtle.domain.Account.query.dto.response.HoldingDTO;
import com.team.goldenturtle.domain.Account.query.dto.response.TradeDTO;
import com.team.goldenturtle.domain.Account.query.dto.response.GameSessionDTO;
import org.apache.ibatis.annotations.Mapper;

import java.math.BigDecimal;
import java.util.List;

@Mapper
public interface AccountMapper {

    List<HoldingDTO> findHoldingsByUserId(AccountSearchRequest accountSearchRequest);
    long countHoldings(AccountSearchRequest accountSearchRequest);
    long countTrades(AccountSearchRequest accountSearchRequest);
    List<TradeDTO> findTradesBySessionId(AccountSearchRequest accountSearchRequest);
    BigDecimal selectCashBalanceByUserId (Long userId);
    List<GameSessionDTO> findGameSessionByUserId(AccountSearchRequest accountSearchRequest);
    long countGameSession(AccountSearchRequest accountSearchRequest);
}
