package com.thy.scrum_planning_be.service;

import com.thy.scrum_planning_be.dto.VoteRequest;
import com.thy.scrum_planning_be.entity.Vote;
import com.thy.scrum_planning_be.repository.VoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class VoteService {

    private final VoteRepository voteRepository;
    private final WebSocketService webSocketService;

    @Transactional
    public void vote(UUID roomId, UUID taskId, VoteRequest request) {
        Optional<Vote> existingVote = voteRepository.findByTaskIdAndUserId(taskId, request.getUserId());
        
        Vote vote;
        if (existingVote.isPresent()) {
            vote = existingVote.get();
            vote.setPoint(request.getPoint());
        } else {
            vote = Vote.builder()
                    .taskId(taskId)
                    .userId(request.getUserId())
                    .username(request.getUsername())
                    .point(request.getPoint())
                    .build();
        }
        voteRepository.save(vote);

        // Notify room that user voted (hide point)
        webSocketService.notifyRoom(roomId, "USER_VOTED", request.getUserId());
    }

    @Transactional
    public List<Vote> revealVotes(UUID roomId, UUID taskId) {
        List<Vote> votes = voteRepository.findByTaskId(taskId);
        votes.forEach(v -> v.setRevealed(true));
        voteRepository.saveAll(votes);

        webSocketService.notifyRoom(roomId, "VOTES_REVEALED", votes);
        return votes;
    }

    @Transactional
    public void revote(UUID roomId, UUID taskId) {
        voteRepository.deleteByTaskId(taskId);
        webSocketService.notifyRoom(roomId, "REVOTE_STARTED", taskId);
    }
}
