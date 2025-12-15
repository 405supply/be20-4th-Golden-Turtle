package com.team.goldenturtle.trade.command.application.service;

import com.team.goldenturtle.trade.command.application.dto.request.BuyStockCommandRequestDto;
import com.team.goldenturtle.trade.command.application.dto.request.SellStockCommandRequestDto;

public interface TradeCommandService {

    void handleBuyCommand(Long userId, BuyStockCommandRequestDto request);//  주식 매수 요청 처리

    void handleSellCommand(Long userId, SellStockCommandRequestDto request);//  주식 매도 요청 처리
}
