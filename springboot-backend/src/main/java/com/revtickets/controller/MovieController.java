package com.revtickets.controller;

import com.revtickets.model.Event;
import com.revtickets.model.Show;
import com.revtickets.service.EventService;
import com.revtickets.repository.ShowRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Arrays;

@RestController
@RequestMapping("/api/movies")
@CrossOrigin(origins = "http://localhost:5173")
public class MovieController {

    @Autowired
    private EventService eventService;
    
    @Autowired
    private ShowRepository showRepository;

    @GetMapping
    public ResponseEntity<List<Event>> getAllMovies() {
        List<Event> movies = eventService.getEventsByCategory("movie");
        return ResponseEntity.ok(movies);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Event> getMovieById(@PathVariable Long id) {
        Event movie = eventService.getEventById(id);
        return movie != null ? ResponseEntity.ok(movie) : ResponseEntity.notFound().build();
    }

    @GetMapping("/search")
    public ResponseEntity<List<Event>> searchMovies(@RequestParam String q) {
        List<Event> movies = eventService.searchEvents(q, "movie");
        return ResponseEntity.ok(movies);
    }

    @GetMapping("/filter")
    public ResponseEntity<List<Event>> filterMovies(
            @RequestParam(required = false) String language,
            @RequestParam(required = false) Double minRating) {
        List<Event> movies = eventService.filterMovies(language, minRating);
        return ResponseEntity.ok(movies);
    }
    
    @GetMapping("/{id}/shows")
    public ResponseEntity<List<Show>> getShowsByMovieId(@PathVariable Long id) {
        // Create mock shows for demo
        List<Show> shows = Arrays.asList(
            createShow(id, "PVR Cinemas", "10:00 AM", "2024-01-15", 250.0),
            createShow(id, "INOX", "02:30 PM", "2024-01-15", 280.0),
            createShow(id, "Cinepolis", "06:00 PM", "2024-01-15", 300.0),
            createShow(id, "PVR Cinemas", "09:30 PM", "2024-01-15", 320.0)
        );
        return ResponseEntity.ok(shows);
    }
    
    private Show createShow(Long movieId, String theater, String showTime, String showDate, Double price) {
        Show show = new Show();
        show.setMovieId(movieId);
        show.setTheater(theater);
        show.setShowTime(showTime);
        show.setShowDate(showDate);
        show.setPrice(price);
        show.setTotalSeats(100);
        show.setAvailableSeats(75);
        return show;
    }
}