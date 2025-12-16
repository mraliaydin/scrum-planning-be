package com.thy.scrum_planning_be.config;

import com.thy.scrum_planning_be.dto.WebSocketMessage;
import com.thy.scrum_planning_be.service.RoomService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.UUID;

@Component
@Slf4j
@RequiredArgsConstructor
public class WebSocketEventListener {

    private final RoomService roomService;
    private final SimpMessagingTemplate messagingTemplate;

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());

        // Session attributes'dan userId ve roomId'yi al
        if (headerAccessor.getSessionAttributes() != null) {
            UUID userId = (UUID) headerAccessor.getSessionAttributes().get("userId");
            UUID roomId = (UUID) headerAccessor.getSessionAttributes().get("roomId");

            if (userId != null && roomId != null) {
                log.info("User Disconnected: " + userId);

                try {
                    // Kullanıcıyı veritabanından sil (RoomService.leaveRoom mantığı)
                    // Not: leaveRoom metodu transactional olduğu için güvenlidir.
                    // Ancak kullanıcı zaten silinmişse hata fırlatabilir, try-catch ile sarmalamak iyidir.
                    roomService.leaveRoom(userId);

                    // Odadaki diğer kullanıcılara haber ver
                    WebSocketMessage message = new WebSocketMessage("USER_LEFT", userId);
                    messagingTemplate.convertAndSend("/topic/rooms/" + roomId, message);

                } catch (Exception e) {
                    log.warn("Error removing user on disconnect: " + e.getMessage());
                }
            }
        }
    }
}
