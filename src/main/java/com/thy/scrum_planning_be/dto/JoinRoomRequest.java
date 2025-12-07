package com.thy.scrum_planning_be.dto;

import lombok.Data;

@Data
public class JoinRoomRequest {
    private String roomName;
    private String username;
}
