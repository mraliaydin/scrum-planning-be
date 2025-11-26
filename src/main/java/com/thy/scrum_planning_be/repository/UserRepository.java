package com.thy.scrum_planning_be.repository;

import com.thy.scrum_planning_be.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    List<User> findByRoomId(UUID roomId);
}
