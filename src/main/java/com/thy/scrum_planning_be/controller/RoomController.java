package com.thy.scrum_planning_be.controller;

import com.thy.scrum_planning_be.aspect.Loggable;
import com.thy.scrum_planning_be.dto.CreateRoomResponse;
import com.thy.scrum_planning_be.dto.RoomRequest;
import com.thy.scrum_planning_be.dto.TaskWithVotesResponse;
import com.thy.scrum_planning_be.entity.Room;
import com.thy.scrum_planning_be.entity.Task;
import com.thy.scrum_planning_be.entity.User;
import com.thy.scrum_planning_be.service.RoomService;
import com.thy.scrum_planning_be.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/rooms")
@RequiredArgsConstructor
public class RoomController {

    private final RoomService roomService;
    private final TaskService taskService;

    // Odayı oluşturmak mecburen HTTP/REST olmalı (Link üretimi için)
    @Loggable
    @PostMapping
    public ResponseEntity<CreateRoomResponse> createRoom(@RequestBody RoomRequest request) {
        return ResponseEntity.ok(roomService.createRoom(request));
    }

    // Sayfa ilk yüklendiğinde oda bilgisini çekmek için
    @Loggable
    @GetMapping("/{roomId}")
    public ResponseEntity<Room> getRoom(@PathVariable UUID roomId) {
        return ResponseEntity.ok(roomService.getRoom(roomId));
    }

    // Sayfa ilk yüklendiğinde içerideki kullanıcıları listelemek için
    @Loggable
    @GetMapping("/{roomId}/users")
    public ResponseEntity<List<User>> getRoomUsers(@PathVariable UUID roomId) {
        return ResponseEntity.ok(roomService.getRoomUsers(roomId));
    }

    // Sayfa ilk yüklendiğinde aktif bir oylama varsa onu göstermek için
    @Loggable
    @GetMapping("/{roomId}/tasks/current")
    public ResponseEntity<TaskWithVotesResponse> getActiveTask(@PathVariable UUID roomId) {
        return taskService.getActiveTask(roomId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.noContent().build());
    }

    // Geçmiş oylamaları listelemek için
    @Loggable
    @GetMapping("/{roomId}/history")
    public ResponseEntity<List<Task>> getHistory(@PathVariable UUID roomId) {
        return ResponseEntity.ok(taskService.getTaskHistory(roomId));
    }
}