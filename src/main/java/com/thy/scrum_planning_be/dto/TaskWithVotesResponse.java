package com.thy.scrum_planning_be.dto;

import com.thy.scrum_planning_be.entity.Task;
import com.thy.scrum_planning_be.entity.Vote;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskWithVotesResponse {
    private Task task;
    private List<Vote> votes;
    private boolean isRevealed;
}
