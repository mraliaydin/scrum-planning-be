package com.thy.scrum_planning_be.dto;

import lombok.Data;
import java.util.UUID;

@Data
public class CompleteTaskRequest {
    private UUID taskId;
    private String finalPoint;
}