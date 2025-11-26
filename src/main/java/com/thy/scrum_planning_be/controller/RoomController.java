package com.thy.scrum_planning_be.controller;

import com.thy.scrum_planning_be.dto.*;
import com.thy.scrum_planning_be.entity.Room;
import com.thy.scrum_planning_be.entity.Task;
import com.thy.scrum_planning_be.entity.User;
import com.thy.scrum_planning_be.entity.Vote;
import com.thy.scrum_planning_be.service.RoomService;
import com.thy.scrum_planning_be.service.TaskService;
import com.thy.scrum_planning_be.service.VoteService;
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
    private final VoteService voteService;

    @PostMapping
    public ResponseEntity<CreateRoomResponse> createRoom(@RequestBody RoomRequest request) {
        return ResponseEntity.ok(roomService.createRoom(request));
    }

    @GetMapping("/{roomId}")
    public ResponseEntity<Room> getRoom(@PathVariable UUID roomId) {
        return ResponseEntity.ok(roomService.getRoom(roomId));
    }

    @GetMapping("/{roomId}/users")
    public ResponseEntity<List<User>> getRoomUsers(@PathVariable UUID roomId) {
        return ResponseEntity.ok(roomService.getRoomUsers(roomId));
    }

    @PostMapping("/{roomId}/join")
    public ResponseEntity<User> joinRoom(@PathVariable UUID roomId, @RequestBody JoinRoomRequest request) {
        return ResponseEntity.ok(roomService.joinRoom(roomId, request));
    }

    @DeleteMapping("/{roomId}/leave")
    public ResponseEntity<Void> leaveRoom(@PathVariable UUID roomId, @RequestParam UUID userId) {
        roomService.leaveRoom(roomId, userId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{roomId}/kick/{userId}")
    public ResponseEntity<Void> kickUser(@PathVariable UUID roomId, @PathVariable UUID userId) {
        roomService.kickUser(roomId, userId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{roomId}/tasks")
    public ResponseEntity<Task> createTask(@PathVariable UUID roomId, @RequestBody TaskRequest request) {
        return ResponseEntity.ok(taskService.createTask(roomId, request));
    }

    @GetMapping("/{roomId}/tasks/current")
    public ResponseEntity<Task> getActiveTask(@PathVariable UUID roomId) {
        return taskService.getActiveTask(roomId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.noContent().build());
    }

    @PostMapping("/{roomId}/tasks/{taskId}/vote")
    public ResponseEntity<Void> vote(@PathVariable UUID roomId, @PathVariable UUID taskId, @RequestBody VoteRequest request) {
        voteService.vote(roomId, taskId, request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{roomId}/tasks/{taskId}/reveal")
    public ResponseEntity<List<Vote>> revealVotes(@PathVariable UUID roomId, @PathVariable UUID taskId) {
        return ResponseEntity.ok(voteService.revealVotes(roomId, taskId));
    }

    @PostMapping("/{roomId}/tasks/{taskId}/revote")
    public ResponseEntity<Void> revote(@PathVariable UUID roomId, @PathVariable UUID taskId) {
        voteService.revote(roomId, taskId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{roomId}/tasks/{taskId}/complete")
    public ResponseEntity<Task> completeTask(@PathVariable UUID roomId, @PathVariable UUID taskId, @RequestBody String finalPoint) {
        return ResponseEntity.ok(taskService.completeTask(roomId, taskId, finalPoint));
    }

    @GetMapping("/{roomId}/history")
    public ResponseEntity<List<Task>> getHistory(@PathVariable UUID roomId) {
        return ResponseEntity.ok(taskService.getTaskHistory(roomId));
    }
}
