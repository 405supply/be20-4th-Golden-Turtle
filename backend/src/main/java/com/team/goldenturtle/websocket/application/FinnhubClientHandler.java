package com.team.goldenturtle.websocket.application;

import com.team.goldenturtle.ranking.command.application.service.RankRealtimeService;
import com.team.goldenturtle.trade.command.application.service.RealtimeStockPriceProvider;
import com.team.goldenturtle.websocket.application.service.StompBroadcastService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

@Component
@Slf4j
@RequiredArgsConstructor
public class FinnhubClientHandler extends TextWebSocketHandler {

    // 멀티스레드 환경에서 session 참조의 race condition을 방지하기 위해서 AtomicReference 사용
    private final AtomicReference<WebSocketSession> sessionRef = new AtomicReference<>();
    private final AtomicBoolean isClosed = new AtomicBoolean(true);
    private final ObjectMapper objectMapper;
    private final StompBroadcastService stompBroadcastService;
    private final RealtimeStockPriceProvider realtimeStockPriceProvider; // Provider 주입
    private final RankRealtimeService rankRealtimeService;
    // 랭킹 계산에 필요한 sessionId 저장
    private final Set<Long> activeSessionIds = ConcurrentHashMap.newKeySet();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        sessionRef.set(session);
        isClosed.set(false);

        log.info("Finnhub websocket 연결 수립 성공. URI : {}", session.getUri());
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();

        JsonNode payloadJson = objectMapper.readTree(payload);
        String messageType = payloadJson.path("type").asString();

        switch (messageType) {
            case "ping":
                log.info("Finnhub websocket 핑 수신됨");
                return;
            case "trade":
                BigDecimal leveragedPrice;
                String newTicker;
                Long timestamp;
                String newPayload = "";

                // trade 메시지 받았을 때, Provider에 가격을 업데이트 하는 로직
                JsonNode tradeData = payloadJson.path("data");
                if (tradeData.isArray()) {
                    JsonNode trade = tradeData.get(0);

                    String ticker = trade.path("s").asString();   // 종목코드
                    BigDecimal price = BigDecimal.valueOf(trade.path("p").asDouble());  // 현재가
                    timestamp = trade.path("t").asLong();

                    leveragedPrice = leveragedPrice(price, ticker);
                    newTicker = ticker + "X5";

                    realtimeStockPriceProvider.updatePrice(newTicker, leveragedPrice, timestamp);
                    realtimeStockPriceProvider.updatePrice(ticker, price, timestamp);

                    newPayload = String.format(
                            "{\"type\":\"trade\",\"data\":[{\"s\":\"%s\",\"p\":%s,\"t\":%d,\"v\":1}]}",
                            newTicker,
                            leveragedPrice.toPlainString(),  // 소수점 그대로 유지
                            timestamp
                    );
                }

                //랭킹 계산
                for (Long sessionId : activeSessionIds) {
                    rankRealtimeService.updateMyRank(sessionId);
                }
                String destination = "/topic/stock";
                if (!newPayload.equals("")) {
                stompBroadcastService.broadcast(destination, newPayload);
                }

                //stomp를 통해 프론트엔드로 브로드캐스팅

                stompBroadcastService.broadcast(destination, payload);

                return;

            default:
                log.warn("Finnhub websocket 처리되지 않은 메시지 수신됨 : {}", payload);
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        isClosed.set(true);
        log.warn("Finnhub websocket 연결 종료됨. 코드 : {} 원인 : {}", status.getCode(), status.getReason());
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        log.error("Finnhub 전송 오류 발생 : {}", exception.getMessage());
    }

    private MessageResult sendMessage(String type, String ticker) {
        WebSocketSession session = sessionRef.get();

        if (session == null || !session.isOpen()) {
            log.error("Finnhub websocket 메시지를 전송할 수 없습니다. Session이 열려있지 않음.");
            return MessageResult.SESSION_NOT_READY;
        }

        String json = "{\"type\":\"" + type + "\",\"symbol\":\"" + ticker + "\"}";

        try {
            session.sendMessage(new TextMessage(json));
        }
        catch (Exception e) {
            log.error("Finnhub websocket 요청 전송 실패 : {}", e.getMessage());
            return MessageResult.SEND_FAILED;
        }

        log.info("Finnhub websocket 메시지 전송 성공 : {} {}", type, ticker);
        return MessageResult.SUCCESS;
    }

    public MessageResult subscribe(String ticker) {
        return sendMessage("subscribe", ticker);
    }

    public MessageResult unsubscribe(String ticker) {
        return sendMessage("unsubscribe", ticker);
    }

    public void registerSession(Long sessionId) {
        activeSessionIds.add(sessionId);
        log.info("[FINNHUB] Session 등록됨: {}", sessionId);
    }

    public void unregisterSession(Long sessionId) {
        activeSessionIds.remove(sessionId);
        log.info("[FINNHUB] Session 제거됨: {}", sessionId);
    }
    public enum MessageResult {
        SUCCESS,
        SESSION_NOT_READY,
        SEND_FAILED
    }

    public BigDecimal leveragedPrice(BigDecimal price, String ticker) {
        BigDecimal prevPrice = realtimeStockPriceProvider.getPrice(ticker).orElse(price);

        BigDecimal delta = (price.subtract(prevPrice).multiply(BigDecimal.valueOf(10)));

        return price.add(delta);
    }
}
