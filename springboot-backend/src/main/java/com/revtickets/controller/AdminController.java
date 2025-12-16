package com.revtickets.controller;

import com.revtickets.model.Event;
import com.revtickets.model.Booking;
import com.revtickets.service.EventService;
import com.revtickets.service.BookingService;
import com.revtickets.repository.ShowRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
public class AdminController {

    @Autowired
    private EventService eventService;

    @Autowired
    private BookingService bookingService;

    @Autowired
    private ShowRepository showRepository;

    // Get dashboard stats
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStats() {
        Map<String, Object> stats = new HashMap<>();
        
        try {
            List<Event> events = eventService.getAllEvents();
            List<com.revtickets.model.Show> shows = showRepository.findAll();
            List<Booking> bookings = bookingService.getAllBookings();
            
            // Calculate total revenue
            double totalRevenue = bookings.stream()
                .mapToDouble(Booking::getTotalPrice)
                .sum();
            
            stats.put("totalEvents", events.size());
            stats.put("totalMovies", shows.size());
            stats.put("totalBookings", bookings.size());
            stats.put("totalRevenue", totalRevenue);
            
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // Event Management
    @PostMapping("/events")
    public ResponseEntity<Event> createEvent(@RequestBody Event event) {
        try {
            Event created = eventService.createEvent(event);
            return ResponseEntity.ok(created);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/events/{id}")
    public ResponseEntity<Event> updateEvent(@PathVariable Long id, @RequestBody Event event) {
        try {
            event.setId(id);
            Event updated = eventService.updateEvent(event);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/events/{id}")
    public ResponseEntity<Void> deleteEvent(@PathVariable Long id) {
        try {
            eventService.deleteEvent(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Travel Management
    @Autowired
    private com.revtickets.service.TravelService travelService;

    @GetMapping("/travels")
    public ResponseEntity<List<com.revtickets.model.Travel>> getAllTravels() {
        try {
            List<com.revtickets.model.Travel> travels = travelService.getAllTravels();
            return ResponseEntity.ok(travels);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/travels")
    public ResponseEntity<com.revtickets.model.Travel> createTravel(@RequestBody com.revtickets.model.Travel travel) {
        try {
            com.revtickets.model.Travel created = travelService.createTravel(travel);
            return ResponseEntity.ok(created);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/travels/{id}")
    public ResponseEntity<com.revtickets.model.Travel> updateTravel(@PathVariable Long id, @RequestBody com.revtickets.model.Travel travel) {
        try {
            travel.setId(id);
            com.revtickets.model.Travel updated = travelService.updateTravel(travel);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/travels/{id}")
    public ResponseEntity<Void> deleteTravel(@PathVariable Long id) {
        try {
            travelService.deleteTravel(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Get all bookings for admin
    @GetMapping("/bookings")
    public ResponseEntity<List<Booking>> getAllBookings() {
        try {
            List<Booking> bookings = bookingService.getAllBookings();
            return ResponseEntity.ok(bookings);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
