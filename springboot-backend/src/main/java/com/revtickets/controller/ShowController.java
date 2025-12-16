package com.revtickets.controller;

import com.revtickets.model.Show;
import com.revtickets.service.ShowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/shows")
@CrossOrigin(origins = "http://localhost:5173")
public class ShowController {

    @Autowired
    private ShowService showService;

    @GetMapping
    public ResponseEntity<List<Show>> getAllShows() {
        return ResponseEntity.ok(showService.getAllShows());
    }

    @GetMapping("/movie/{movieId}")
    public ResponseEntity<List<Show>> getShowsByMovie(@PathVariable Long movieId) {
        return ResponseEntity.ok(showService.getShowsByMovieId(movieId));
    }
    
    @GetMapping("/event/{eventId}")
    public ResponseEntity<List<Show>> getShowsByEvent(@PathVariable Long eventId) {
        return ResponseEntity.ok(showService.getShowsByEventId(eventId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Show> getShow(@PathVariable Long id) {
        Show show = showService.getShowById(id);
        return show != null ? ResponseEntity.ok(show) : ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<Show> createShow(@RequestBody Show show) {
        return ResponseEntity.ok(showService.createShow(show));
    }

    @GetMapping("/{id}/booked-seats")
    public ResponseEntity<List<String>> getBookedSeats(@PathVariable Long id) {
        List<String> bookedSeats = showService.getBookedSeats(id);
        return ResponseEntity.ok(bookedSeats);
    }

    @GetMapping("/{id}/seat-status")
    public ResponseEntity<java.util.Map<String, Object>> getSeatStatus(@PathVariable Long id) {
        Show show = showService.getShowById(id);
        if (show == null) {
            return ResponseEntity.notFound().build();
        }
        
        java.util.Map<String, Object> response = new java.util.HashMap<>();
        response.put("totalSeats", show.getTotalSeats());
        response.put("availableSeats", show.getAvailableSeats());
        response.put("bookedSeats", show.getBookedSeats() != null ? show.getBookedSeats() : new java.util.ArrayList<>());
        
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/check-seats")
    public ResponseEntity<java.util.Map<String, Object>> checkSeatAvailability(
            @PathVariable Long id, 
            @RequestBody java.util.List<String> seats) {
        
        java.util.Map<String, Object> response = showService.checkSeatAvailability(id, seats);
        return ResponseEntity.ok(response);
    }
}
