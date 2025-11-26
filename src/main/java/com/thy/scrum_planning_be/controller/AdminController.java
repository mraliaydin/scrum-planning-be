package com.thy.scrum_planning_be.controller;

import com.thy.scrum_planning_be.entity.Room;
import com.thy.scrum_planning_be.entity.User;
import com.thy.scrum_planning_be.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/admin/rooms")
@RequiredArgsConstructor
public class AdminController {

    private final RoomService roomService;

    @GetMapping
    public ResponseEntity<List<Room>> getAllRooms() {
        return ResponseEntity.ok(roomService.getAllRooms());
    }

    @DeleteMapping("/{roomId}")
    public ResponseEntity<Void> deleteRoom(@PathVariable UUID roomId) {
        roomService.deleteRoom(roomId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{roomId}/kick/{userId}")
    public ResponseEntity<Void> kickUser(@PathVariable UUID roomId, @PathVariable UUID userId) {
        roomService.kickUser(roomId, userId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{roomId}/users")
    public ResponseEntity<List<User>> getRoomUsers(@PathVariable UUID roomId) {
        return ResponseEntity.ok(roomService.getRoomUsers(roomId));
    }
}
