package com.revtickets.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document(collection = "reviews")
@Data
@NoArgsConstructor
public class Review {
    @Id
    private String id;
    
    private Long userId; // MySQL Ref
    private String userName;
    private Long eventId; // MySQL Ref (Event/Movie)
    private Double rating;
    private String comment;
    private int likes;
    private LocalDateTime timestamp;

    public Review(Long userId, String userName, Long eventId, Double rating, String comment) {
        this.userId = userId;
        this.userName = userName;
        this.eventId = eventId;
        this.rating = rating;
        this.comment = comment;
        this.likes = 0;
        this.timestamp = LocalDateTime.now();
    }
}
