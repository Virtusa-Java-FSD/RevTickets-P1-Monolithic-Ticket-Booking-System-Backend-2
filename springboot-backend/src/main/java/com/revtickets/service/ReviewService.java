package com.revtickets.service;

import com.revtickets.model.Review;
import com.revtickets.repository.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReviewService {
    
    @Autowired
    private ReviewRepository reviewRepository;
    
    public Review addReview(Review review) {
        review.setTimestamp(LocalDateTime.now());
        review.setLikes(0);
        return reviewRepository.save(review);
    }
    
    public List<Review> getReviewsByEventId(Long eventId) {
        return reviewRepository.findByEventId(eventId);
    }
    
    public List<Review> getReviewsByUserId(Long userId) {
        return reviewRepository.findByUserId(userId);
    }
    
    public Review likeReview(String reviewId) {
        Review review = reviewRepository.findById(reviewId).orElse(null);
        if (review != null) {
            review.setLikes(review.getLikes() + 1);
            return reviewRepository.save(review);
        }
        return null;
    }
}
