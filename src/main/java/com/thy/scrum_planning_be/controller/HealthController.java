package com.thy.scrum_planning_be.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/health")
@RequiredArgsConstructor
public class HealthController {

    @GetMapping
    public ResponseEntity<String> getHealth() {
        return ResponseEntity.ok("Scrum Planning API is running.");
    }
}
