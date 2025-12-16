package com.revtickets.controller;

import com.revtickets.model.Review;
import com.revtickets.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/reviews")
@CrossOrigin(origins = "http://localhost:5173")
public class ReviewController {
    
    @Autowired
    private ReviewService reviewService;
    
    @PostMapping
    public ResponseEntity<?> addReview(@RequestBody Review review) {
        try {
            return ResponseEntity.ok(reviewService.addReview(review));
        } catch (Exception e) {
            e.printStackTrace(); // Print to console
            return ResponseEntity.internalServerError().body(java.util.Map.of("error", e.getMessage(), "trace", e.toString()));
        }
    }
    
    @GetMapping("/event/{eventId}")
    public ResponseEntity<List<Review>> getEventReviews(@PathVariable Long eventId) {
        return ResponseEntity.ok(reviewService.getReviewsByEventId(eventId));
    }
    
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Review>> getUserReviews(@PathVariable Long userId) {
        return ResponseEntity.ok(reviewService.getReviewsByUserId(userId));
    }
    
    @PutMapping("/{reviewId}/like")
    public ResponseEntity<?> likeReview(@PathVariable String reviewId) {
        try {
            Review review = reviewService.likeReview(reviewId);
            if (review != null) {
                return ResponseEntity.ok(review);
            }
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(java.util.Map.of("error", e.getMessage()));
        }
    }
}
