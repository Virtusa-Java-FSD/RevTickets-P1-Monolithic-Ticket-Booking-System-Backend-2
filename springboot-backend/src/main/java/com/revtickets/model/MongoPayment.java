package com.revtickets.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.Data;
import java.time.LocalDateTime;

@Document(collection = "payments")
@Data
public class MongoPayment {
    @Id
    private String id;
    private String orderId;
    private String paymentId;
    private Double amount;
    private String status;
    private LocalDateTime timestamp;
}