package com.revtickets.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "travels")
@Data
@NoArgsConstructor
public class Travel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String type; // "flight", "bus", "train"
    private String operator;
    private String vehicleNumber;
    private String departure;
    private String arrival;
    private String departureTime;
    private String arrivalTime;
    private String duration;
    private Double price;
    private Integer availableSeats;
    private Double rating;
    
    @ElementCollection
    private String[] amenities;
}