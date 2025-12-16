package com.revtickets.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document(collection = "notifications")
@Data
@NoArgsConstructor
public class Notification {
    @Id
    private String id;
    
    private Long userId; // Reference to MySQL User
    private String message;
    private String type; // e.g., "BOOKING", "OFFER"
    private boolean read;
    private LocalDateTime timestamp;

    public Notification(Long userId, String message, String type) {
        this.userId = userId;
        this.message = message;
        this.type = type;
        this.read = false;
        this.timestamp = LocalDateTime.now();
    }
}
