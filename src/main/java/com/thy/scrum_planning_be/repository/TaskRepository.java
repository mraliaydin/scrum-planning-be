package com.thy.scrum_planning_be.repository;

import com.thy.scrum_planning_be.entity.Task;
import com.thy.scrum_planning_be.entity.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TaskRepository extends JpaRepository<Task, UUID> {
    List<Task> findByRoomIdAndStatusOrderByCompletedAtDesc(UUID roomId, TaskStatus status);

    Optional<Task> findByRoomIdAndStatus(UUID roomId, TaskStatus status);
}
