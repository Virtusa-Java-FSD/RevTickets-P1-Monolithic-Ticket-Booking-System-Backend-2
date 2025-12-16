package com.revtickets.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "tickets")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Ticket {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "ticket_id")
    private String ticketId; // unique alphanumeric id
    
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user; // Linked to User
    
    @Column(name = "ticket_type")
    private String ticketType; // "BUS", "MOVIE", "EVENT"
    
    private String title; // Movie Title or Bus Operator Name
    
    @Column(name = "from_location")
    private String fromLocation;
    
    @Column(name = "to_location")
    private String toLocation;
    
    @Column(name = "journey_date_time")
    private LocalDateTime journeyDateTime;
    
    @Column(name = "seat_numbers")
    private String seatNumbers; // e.g. "A1,A2"
    
    @Column(name = "seat_class")
    private String seatClass; // e.g. "Sleeper", "Gold"
    
    @Column(name = "passenger_count")
    private Integer passengerCount;
    
    @Column(name = "total_amount")
    private Double totalAmount;
    
    private String status; // CONFIRMED, CANCELLED
    
    @Column(name = "order_id")
    private String orderId; // Razorpay Order ID
    
    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
}
