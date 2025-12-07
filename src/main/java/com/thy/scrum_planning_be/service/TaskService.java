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

    @Transactional
    public Task createTask(UUID roomId, TaskRequest request) {
        // Varsa mevcut aktif taskı bul ve tamamlandı olarak işaretle
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

        return taskRepository.save(task);
    }

    public Optional<Task> getActiveTask(UUID roomId) {
        return taskRepository.findByRoomIdAndStatus(roomId, TaskStatus.ACTIVE);
    }

    @Transactional
    public Task completeTask(UUID taskId, String finalPoint) {
        // roomId parametresini kaldırdık, sadece task işlemi yapıyoruz.
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        task.setStatus(TaskStatus.COMPLETED);
        task.setCompletedAt(LocalDateTime.now());
        task.setFinalPoint(finalPoint);

        return taskRepository.save(task);
    }

    public List<Task> getTaskHistory(UUID roomId) {
        return taskRepository.findByRoomIdAndStatusOrderByCompletedAtDesc(roomId, TaskStatus.COMPLETED);
    }
}