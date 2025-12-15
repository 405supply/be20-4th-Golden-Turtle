package com.team.goldenturtle.websocket.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StompBroadcastService {

    private final SimpMessagingTemplate simpMessagingTemplate;

    public void broadcast(String destination, String message) {
        simpMessagingTemplate.convertAndSend(destination, message);
    }
}
