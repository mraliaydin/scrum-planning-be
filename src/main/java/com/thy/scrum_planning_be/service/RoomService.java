package com.thy.scrum_planning_be.service;

import com.thy.scrum_planning_be.dto.CreateRoomResponse;
import com.thy.scrum_planning_be.dto.JoinRoomRequest;
import com.thy.scrum_planning_be.dto.RoomRequest;
import com.thy.scrum_planning_be.entity.Room;
import com.thy.scrum_planning_be.entity.User;
import com.thy.scrum_planning_be.repository.RoomRepository;
import com.thy.scrum_planning_be.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RoomService {

    private final RoomRepository roomRepository;
    private final UserRepository userRepository;
    private final WebSocketService webSocketService;

    @Transactional
    public CreateRoomResponse createRoom(RoomRequest request) { // Return tipi değişti
        Room room = Room.builder()
                .name(request.getRoomName())
                .createdBy(request.getUsername())
                .build();
        room = roomRepository.save(room);

        // Creator joins automatically as moderator
        User user = User.builder()
                .username(request.getUsername())
                .roomId(room.getId())
                .isModerator(true)
                .build();
        user = userRepository.save(user);

        // Hem odayı hem kullanıcıyı dönüyoruz
        return CreateRoomResponse.builder()
                .room(room)
                .creator(user)
                .build();
    }

    @Transactional
    public User joinRoom(UUID roomId, JoinRoomRequest request) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("Room not found"));

        if (!room.isActive()) {
            throw new RuntimeException("Room is not active");
        }

        User user = User.builder()
                .username(request.getUsername())
                .roomId(roomId)
                .isModerator(false)
                .build();
        user = userRepository.save(user);

        webSocketService.notifyRoom(roomId, "USER_JOINED", user);
        return user;
    }

    @Transactional
    public void leaveRoom(UUID roomId, UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        userRepository.delete(user);
        webSocketService.notifyRoom(roomId, "USER_LEFT", userId);
    }

    @Transactional
    public void kickUser(UUID roomId, UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        userRepository.delete(user);
        webSocketService.notifyRoom(roomId, "USER_KICKED", userId);
    }

    public Room getRoom(UUID roomId) {
        return roomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("Room not found"));
    }

    public List<User> getRoomUsers(UUID roomId) {
        return userRepository.findByRoomId(roomId);
    }
    
    public List<Room> getAllRooms() {
        return roomRepository.findAll();
    }

    @Transactional
    public void deleteRoom(UUID roomId) {
        roomRepository.deleteById(roomId);
    }
}
