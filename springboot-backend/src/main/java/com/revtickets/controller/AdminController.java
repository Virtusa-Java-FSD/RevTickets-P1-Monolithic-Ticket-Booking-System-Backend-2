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

    @Autowired
    private com.revtickets.repository.UserRepository userRepository;

    @Autowired
    private com.revtickets.repository.TheaterRepository theaterRepository;

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStats() {
        Map<String, Object> stats = new HashMap<>();
        
        try {
            List<Event> events = eventService.getAllEvents();
            List<com.revtickets.model.Show> shows = showRepository.findAll();
            List<Booking> bookings = bookingService.getAllBookings();
            
            double totalRevenue = bookings.stream()
                .mapToDouble(Booking::getTotalPrice)
                .sum();
            
            stats.put("totalEvents", events.size());
            stats.put("totalMovies", shows.size());
            stats.put("totalBookings", bookings.size());
            stats.put("totalRevenue", totalRevenue);
            
            long totalUsers = userRepository.count();
            stats.put("totalUsers", totalUsers);
            
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/events")
    public ResponseEntity<?> createEvent(@RequestBody Event event) {
        try {
            if (event.getTitle() == null || event.getTitle().trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Title is required"));
            }
            if (event.getDescription() == null || event.getDescription().trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Description is required"));
            }
            if (event.getCategory() == null || event.getCategory().trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Category is required"));
            }
            
            if (event.getRating() == null) {
                event.setRating(0.0);
            }
            
            if (event.getPrice() == null) {
                event.setPrice(0.0);
            }
            
            Event created = eventService.createEvent(event);
            return ResponseEntity.ok(created);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
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

    @GetMapping("/bookings")
    public ResponseEntity<List<Booking>> getAllBookings() {
        try {
            List<Booking> bookings = bookingService.getAllBookings();
            return ResponseEntity.ok(bookings);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/users")
    public ResponseEntity<List<com.revtickets.model.User>> getAllUsers() {
        try {
            List<com.revtickets.model.User> users = userRepository.findAll();
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PutMapping("/users/{id}")
    public ResponseEntity<com.revtickets.model.User> updateUser(@PathVariable Long id, @RequestBody com.revtickets.model.User userDetails) {
        try {
            return userRepository.findById(id)
                    .map(user -> {
                        user.setName(userDetails.getName());
                        user.setEmail(userDetails.getEmail());
                        user.setPhone(userDetails.getPhone());
                        if (userDetails.getRole() != null) {
                            user.setRole(userDetails.getRole());
                        }
                        com.revtickets.model.User updatedUser = userRepository.save(user);
                        return ResponseEntity.ok(updatedUser);
                    })
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        try {
            userRepository.findById(id).ifPresent(userRepository::delete);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/users/{id}/role")
    public ResponseEntity<?> changeUserRole(@PathVariable Long id, @RequestBody Map<String, String> request) {
        try {
            String role = request.get("role");
            return userRepository.findById(id)
                    .map(user -> {
                        if ("ADMIN".equals(role)) {
                            user.setRole(com.revtickets.model.User.UserRole.ADMIN);
                        } else if ("USER".equals(role)) {
                            user.setRole(com.revtickets.model.User.UserRole.USER);
                        }
                        userRepository.save(user);
                        return ResponseEntity.ok(Map.of("message", "User role updated successfully"));
                    })
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/events/{id}/create-shows")
    public ResponseEntity<?> createShowsForEvent(@PathVariable Long id) {
        try {
            Event event = eventService.getEventById(id);
            if (event == null) {
                return ResponseEntity.notFound().build();
            }
            List<com.revtickets.model.Show> existingShows = showRepository.findByEventId(id);
            if (!existingShows.isEmpty()) {
                showRepository.deleteAll(existingShows);
            }
            eventService.createShowsForEvent(event);
            List<com.revtickets.model.Show> newShows = showRepository.findByEventId(id);
            java.util.Set<String> theaterNames = new java.util.HashSet<>();
            for (com.revtickets.model.Show show : newShows) {
                if (show.getTheater() != null) {
                    theaterNames.add(show.getTheater());
                }
            }
            return ResponseEntity.ok(Map.of(
                "message", "Shows created successfully for all theaters",
                "totalShows", newShows.size(),
                "theaters", java.util.List.copyOf(theaterNames)
            ));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/events/{id}/shows-info")
    public ResponseEntity<?> getShowsInfo(@PathVariable Long id) {
        try {
            Event event = eventService.getEventById(id);
            if (event == null) {
                return ResponseEntity.notFound().build();
            }
            List<com.revtickets.model.Show> shows = showRepository.findByEventId(id);
            List<com.revtickets.model.Theater> allTheaters = theaterRepository.findByIsActiveTrue();
            
            java.util.Set<String> theaterNamesInShows = new java.util.HashSet<>();
            for (com.revtickets.model.Show show : shows) {
                if (show.getTheater() != null) {
                    theaterNamesInShows.add(show.getTheater());
                }
            }
            
            java.util.List<String> allTheaterNames = new java.util.ArrayList<>();
            for (com.revtickets.model.Theater theater : allTheaters) {
                allTheaterNames.add(theater.getName() + " (Active: " + theater.getIsActive() + ")");
            }
            
            return ResponseEntity.ok(Map.of(
                "eventId", id,
                "eventTitle", event.getTitle(),
                "totalShows", shows.size(),
                "theatersInShows", java.util.List.copyOf(theaterNamesInShows),
                "allActiveTheaters", allTheaterNames
            ));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
