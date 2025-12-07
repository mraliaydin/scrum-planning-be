package com.thy.scrum_planning_be.service;

import com.thy.scrum_planning_be.aspect.Loggable;
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

    @Loggable
    @Transactional
    public CreateRoomResponse createRoom(RoomRequest request) {
        // VALIDATION: Aynı isimde oda var mı?
        if (roomRepository.existsByName(request.getRoomName())) {
            throw new IllegalArgumentException("Room with name '" + request.getRoomName() + "' already exists.");
        }

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

        return CreateRoomResponse.builder()
                .room(room)
                .creator(user)
                .build();
    }

    @Loggable
    @Transactional
    public User joinRoom(JoinRoomRequest request) {
        // 1. Odayı İSMİNDEN bul
        Room room = roomRepository.findByName(request.getRoomName())
                .orElseThrow(() -> new RuntimeException("Room not found with name: " + request.getRoomName()));

        // 2. Oda aktif mi kontrolü
        if (!room.isActive()) {
            throw new RuntimeException("Cannot join. Room is not active.");
        }

        // --- YENİ EKLENEN KISIM BAŞLANGIÇ ---
        // 3. İsim Çakışması Kontrolü
        // Bu odada aynı isimde başka biri var mı?
        if (userRepository.existsByRoomIdAndUsername(room.getId(), request.getUsername())) {
            throw new IllegalArgumentException("Username '" + request.getUsername() + "' is already taken in this room.");
        }
        // --- YENİ EKLENEN KISIM BİTİŞ ---

        // 4. Kullanıcıyı kaydet
        User user = User.builder()
                .username(request.getUsername())
                .roomId(room.getId())
                .isModerator(false)
                .build();

        return userRepository.save(user);
    }

    @Loggable
    @Transactional
    public void leaveRoom(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

        userRepository.delete(user);
    }

    @Loggable
    @Transactional
    public void kickUser(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

        userRepository.delete(user);
    }

    @Loggable
    public Room getRoom(UUID roomId) {
        return roomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("Room not found with ID: " + roomId));
    }

    @Loggable
    public List<User> getRoomUsers(UUID roomId) {
        // Odanın varlığını kontrol etmek iyi bir pratik olabilir,
        // ancak performans için direkt user sorgusu da yapılabilir.
        // Eğer strict olmak istersen önce getRoom(roomId) çağırabilirsin.
        return userRepository.findByRoomId(roomId);
    }

    @Loggable
    public List<Room> getAllRooms() {
        return roomRepository.findAll();
    }

    @Loggable
    @Transactional
    public void deleteRoom(UUID roomId) {
        if (!roomRepository.existsById(roomId)) {
            throw new RuntimeException("Room not found with ID: " + roomId);
        }
        roomRepository.deleteById(roomId);
    }
}