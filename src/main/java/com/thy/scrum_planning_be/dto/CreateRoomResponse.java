package com.thy.scrum_planning_be.dto;

import com.thy.scrum_planning_be.entity.Room;
import com.thy.scrum_planning_be.entity.User;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreateRoomResponse {
    private Room room;
    private User creator;
}
