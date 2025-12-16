package com.revtickets.controller;

import com.revtickets.model.Travel;
import com.revtickets.service.TravelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/travel")
@CrossOrigin(origins = "http://localhost:5173")
public class TravelController {

    @Autowired
    private TravelService travelService;

    @GetMapping
    public ResponseEntity<List<Travel>> getAllTravels() {
        List<Travel> travels = travelService.getAllTravels();
        return ResponseEntity.ok(travels);
    }

    @GetMapping("/search")
    public ResponseEntity<List<Travel>> searchTravel(
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String from,
            @RequestParam(required = false) String to) {
        List<Travel> results = travelService.searchTravel(type, from, to);
        return ResponseEntity.ok(results);
    }

    @GetMapping("/flights")
    public ResponseEntity<List<Travel>> getFlights(
            @RequestParam String from,
            @RequestParam String to) {
        List<Travel> flights = travelService.getFlights(from, to);
        return ResponseEntity.ok(flights);
    }

    @GetMapping("/buses")
    public ResponseEntity<List<Travel>> getBuses(
            @RequestParam String from,
            @RequestParam String to) {
        List<Travel> buses = travelService.getBuses(from, to);
        return ResponseEntity.ok(buses);
    }

    @GetMapping("/trains")
    public ResponseEntity<List<Travel>> getTrains(
            @RequestParam String from,
            @RequestParam String to) {
        List<Travel> trains = travelService.getTrains(from, to);
        return ResponseEntity.ok(trains);
    }

    @PostMapping("/seed")
    public ResponseEntity<?> seedTravelData() {
        travelService.seedTravelData();
        return ResponseEntity.ok(java.util.Map.of("message", "Travel data seeded successfully", "count", 15));
    }
}