package com.revtickets.controller;

import com.revtickets.model.Event;
import com.revtickets.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/events")
@CrossOrigin(origins = "http://localhost:5173")
public class EventController {
    
    @Autowired
    private EventService eventService;
    
    @GetMapping
    public ResponseEntity<List<Event>> getEvents(@RequestParam(required = false) String category) {
        List<Event> events = category != null ? 
            eventService.getEventsByCategory(category) : 
            eventService.getAllEvents();
        return ResponseEntity.ok(events);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Event> getEvent(@PathVariable Long id) {
        Event event = eventService.getEventById(id);
        return ResponseEntity.ok(event);
    }
    
    @PostMapping
    public ResponseEntity<Event> createEvent(@RequestBody Event event) {
        Event savedEvent = eventService.createEvent(event);
        return ResponseEntity.ok(savedEvent);
    }
    
    @PostMapping("/seed")
    public ResponseEntity<?> seedEvents() {
        eventService.seedDatabase();
        return ResponseEntity.ok(Map.of("message", "Events seeded successfully", "count", 10));
    }
}