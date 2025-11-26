package com.thy.scrum_planning_be.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class VoteRequest {
    private UUID userId;
    private String username;
    private String point;
}
