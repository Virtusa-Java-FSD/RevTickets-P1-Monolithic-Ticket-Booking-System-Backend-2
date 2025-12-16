package com.revtickets.controller;

import com.revtickets.service.EventService;
import com.revtickets.service.TravelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:5173")
public class SeedController {
    
    @Autowired
    private EventService eventService;
    
    @Autowired
    private TravelService travelService;
    
    @PostMapping("/seed")
    public ResponseEntity<?> seedDatabase() {
        eventService.seedDatabase();
        travelService.seedTravelData();
        return ResponseEntity.ok(Map.of("message", "Database seeded with movies, concerts, events, and travel data successfully"));
    }
    
    @GetMapping("/auth/test")
    public ResponseEntity<?> testBackend() {
        return ResponseEntity.ok(Map.of("message", "Backend is running successfully", "status", "OK"));
    }
}