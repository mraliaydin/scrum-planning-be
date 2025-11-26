package com.thy.scrum_planning_be.dto;

import lombok.Data;

@Data
public class RoomRequest {
    private String roomName;
    private String username; // Creator's username
}
