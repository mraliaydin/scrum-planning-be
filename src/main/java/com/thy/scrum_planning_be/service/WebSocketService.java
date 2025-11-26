package com.thy.scrum_planning_be.service;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WebSocketService {

    private final SimpMessagingTemplate messagingTemplate;

    public void notifyRoom(UUID roomId, String type, Object payload) {
        messagingTemplate.convertAndSend("/topic/rooms/" + roomId, new WebSocketMessage(type, payload));
    }

    @Data
    @AllArgsConstructor
    public static class WebSocketMessage {
        private String type;
        private Object payload;
    }
}
