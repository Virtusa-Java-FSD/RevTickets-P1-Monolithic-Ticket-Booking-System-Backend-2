package com.revtickets.controller;

import com.revtickets.model.Theater;
import com.revtickets.repository.TheaterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/theaters")
@CrossOrigin(origins = "*")
public class TheaterController {

    @Autowired
    private TheaterRepository theaterRepository;
    
    @Autowired
    private com.revtickets.service.EventService eventService;

    @GetMapping
    public ResponseEntity<List<Theater>> getAllTheaters() {
        List<Theater> theaters = theaterRepository.findByIsActiveTrue();
        return ResponseEntity.ok(theaters);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Theater> getTheater(@PathVariable Long id) {
        Theater theater = theaterRepository.findById(id).orElse(null);
        return theater != null ? ResponseEntity.ok(theater) : ResponseEntity.notFound().build();
    }

    @GetMapping("/city/{city}")
    public ResponseEntity<List<Theater>> getTheatersByCity(@PathVariable String city) {
        List<Theater> theaters = theaterRepository.findByCity(city);
        return ResponseEntity.ok(theaters);
    }

    @PostMapping
    public ResponseEntity<Theater> createTheater(@RequestBody Theater theater) {
        if (theater.getIsActive() == null) {
            theater.setIsActive(true);
        }
        Theater savedTheater = theaterRepository.save(theater);
        System.out.println("Created theater: " + savedTheater.getName() + " (Active: " + savedTheater.getIsActive() + ")");
        return ResponseEntity.ok(savedTheater);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Theater> updateTheater(@PathVariable Long id, @RequestBody Theater theater) {
        Theater existingTheater = theaterRepository.findById(id).orElse(null);
        if (existingTheater == null) {
            return ResponseEntity.notFound().build();
        }
        theater.setId(id);
        Theater updatedTheater = theaterRepository.save(theater);
        return ResponseEntity.ok(updatedTheater);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTheater(@PathVariable Long id) {
        Theater theater = theaterRepository.findById(id).orElse(null);
        if (theater == null) {
            return ResponseEntity.notFound().build();
        }
        theater.setIsActive(false);
        theaterRepository.save(theater);
        return ResponseEntity.ok(java.util.Map.of("message", "Theater deactivated successfully"));
    }

    @PostMapping("/seed")
    public ResponseEntity<?> seedTheaters() {
        try {
            eventService.seedTheaters();
            long count = theaterRepository.count();
            return ResponseEntity.ok(java.util.Map.of("message", "Theaters seeded successfully", "count", count));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(java.util.Map.of("error", e.getMessage()));
        }
    }
}
