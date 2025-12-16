package com.thy.scrum_planning_be.controller;

import com.thy.scrum_planning_be.aspect.Loggable;
import com.thy.scrum_planning_be.dto.*;
import com.thy.scrum_planning_be.entity.Task;
import com.thy.scrum_planning_be.entity.User;
import com.thy.scrum_planning_be.entity.Vote;
import com.thy.scrum_planning_be.service.RoomService;
import com.thy.scrum_planning_be.service.TaskService;
import com.thy.scrum_planning_be.service.VoteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.UUID;

@Slf4j
@Controller
@RequiredArgsConstructor
public class RoomSocketController {

    private final RoomService roomService;
    private final TaskService taskService;
    private final VoteService voteService;
    private final SimpMessagingTemplate messagingTemplate;

    /**
     * GÜNCELLENDİ: Artık URL'de roomId yok. Genel bir kapıdan giriliyor.
     * Client: /app/join
     * Return: Metodun return ettiği User objesi, isteği atan kişiye
     * "/user/queue/join-response" kanalından gönderilir.
     */
    @Loggable
    @MessageMapping("/join")
    @SendToUser("/queue/join-response") // Cevap sadece isteği atana döner
    public User joinRoom(@Payload JoinRoomRequest request) {
        // Servis artık isim ile buluyor
        User user = roomService.joinRoom(request);

        // 1. Zaten içeride olanlara haber ver (Broadcast)
        // User objesi içinde roomId olduğu için doğru kanala gidecek.
        broadcast(user.getRoomId(), "USER_JOINED", user);

        // 2. İsteği atan kişiye User objesini (ve içindeki roomId'yi) dön.
        // Frontend bu roomId'yi alıp "/topic/rooms/{id}" kanalına abone olacak.
        return user;
    }

    /**
     * Kullanıcı odadan ayrıldığında (veya 'leave' butonuna bastığında).
     * Client: /app/room/{roomId}/leave
     */
    @Loggable
    @MessageMapping("/room/{roomId}/leave")
    public void leaveRoom(@DestinationVariable UUID roomId, @Payload UUID userId) {
        roomService.leaveRoom(userId);
        broadcast(roomId, "USER_LEFT", userId);
    }

    /**
     * Moderatör birini attığında.
     * Client: /app/room/{roomId}/kick
     */
    @Loggable
    @MessageMapping("/room/{roomId}/kick")
    public void kickUser(@DestinationVariable UUID roomId, @Payload UUID userId) {
        roomService.kickUser(userId);
        broadcast(roomId, "USER_KICKED", userId);
    }

    /**
     * Yeni bir task (hikaye) oluşturulduğunda.
     * Client: /app/room/{roomId}/tasks/create
     */
    @Loggable
    @MessageMapping("/room/{roomId}/tasks/create")
    public void createTask(@DestinationVariable UUID roomId, @Payload TaskRequest request) {
        Task task = taskService.createTask(roomId, request);
        broadcast(roomId, "NEW_TASK", task);
    }

    /**
     * Kullanıcı oy verdiğinde.
     * Client: /app/room/{roomId}/vote
     * NOT: VoteRequest içinde 'taskId' alanı olduğundan emin olmalısın.
     */
    @Loggable
    @MessageMapping("/room/{roomId}/vote")
    public void vote(@DestinationVariable UUID roomId, @Payload VoteRequest request) {
        // TaskId request'ten gelmeli. Eğer VoteRequest'te yoksa eklemen gerekir.
        // Ben şu an VoteRequest içinde taskId var varsayımıyla yazıyorum.
        // Eğer yoksa VoteRequest DTO'sunu güncellemelisin.
        voteService.vote(request.getTaskId(), request);

        // Güvenlik: Oylanan puanı herkese hemen göndermiyoruz. Sadece "X kişisi oy verdi" diyoruz.
        broadcast(roomId, "USER_VOTED", request.getUserId());
    }

    /**
     * Oyları aç (Reveal).
     * Client: /app/room/{roomId}/reveal
     */
    @Loggable
    @MessageMapping("/room/{roomId}/reveal")
    public void revealVotes(@DestinationVariable UUID roomId, @Payload UUID taskId) {
        List<Vote> votes = voteService.revealVotes(taskId);
        broadcast(roomId, "VOTES_REVEALED", votes);
    }

    /**
     * Yeniden oylama başlat (Revote).
     * Client: /app/room/{roomId}/revote
     */
    @Loggable
    @MessageMapping("/room/{roomId}/revote")
    public void revote(@DestinationVariable UUID roomId, @Payload UUID taskId) {
        voteService.revote(taskId);
        broadcast(roomId, "REVOTE_STARTED", taskId);
    }

    /**
     * Task'i tamamla ve puanı kesinleştir.
     * Client: /app/room/{roomId}/tasks/complete
     */
    @Loggable
    @MessageMapping("/room/{roomId}/tasks/complete")
    public void completeTask(@DestinationVariable UUID roomId, @Payload CompleteTaskRequest request) {
        Task task = taskService.completeTask(request.getTaskId(), request.getFinalPoint());
        broadcast(roomId, "TASK_COMPLETED", task);
    }

    // --- Helper Method ---

    private void broadcast(UUID roomId, String type, Object payload) {
        messagingTemplate.convertAndSend("/topic/rooms/" + roomId, new WebSocketMessage(type, payload));
    }

    // --- Error Handling ---

    /**
     * Bu controller içindeki herhangi bir metot hata fırlatırsa (örn: RoomService "Room not active" derse)
     * bu metot devreye girer ve hatayı SADECE o kullanıcıya (User Queue) gönderir.
     * Tüm odaya hata mesajı gitmez.
     */
    @MessageExceptionHandler
    @SendToUser("/queue/errors")
    public String handleException(Throwable exception) {
        log.error("Socket Error: ", exception);
        return exception.getMessage();
    }
}
