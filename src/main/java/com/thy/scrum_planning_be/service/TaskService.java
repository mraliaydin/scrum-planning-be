package com.thy.scrum_planning_be.service;

import com.thy.scrum_planning_be.dto.TaskRequest;
import com.thy.scrum_planning_be.entity.Task;
import com.thy.scrum_planning_be.entity.TaskStatus;
import com.thy.scrum_planning_be.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final WebSocketService webSocketService;

    @Transactional
    public Task createTask(UUID roomId, TaskRequest request) {
        // Deactivate current active task if any
        Optional<Task> currentTask = taskRepository.findByRoomIdAndStatus(roomId, TaskStatus.ACTIVE);
        currentTask.ifPresent(task -> {
            task.setStatus(TaskStatus.COMPLETED);
            task.setCompletedAt(LocalDateTime.now());
            taskRepository.save(task);
        });

        Task task = Task.builder()
                .roomId(roomId)
                .name(request.getName())
                .description(request.getDescription())
                .status(TaskStatus.ACTIVE)
                .build();
        task = taskRepository.save(task);

        webSocketService.notifyRoom(roomId, "NEW_TASK", task);
        return task;
    }

    public Optional<Task> getActiveTask(UUID roomId) {
        return taskRepository.findByRoomIdAndStatus(roomId, TaskStatus.ACTIVE);
    }

    @Transactional
    public Task completeTask(UUID roomId, UUID taskId, String finalPoint) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));
        
        task.setStatus(TaskStatus.COMPLETED);
        task.setCompletedAt(LocalDateTime.now());
        task.setFinalPoint(finalPoint);
        task = taskRepository.save(task);

        webSocketService.notifyRoom(roomId, "TASK_COMPLETED", task);
        return task;
    }

    public List<Task> getTaskHistory(UUID roomId) {
        return taskRepository.findByRoomIdAndStatusOrderByCompletedAtDesc(roomId, TaskStatus.COMPLETED);
    }
}
