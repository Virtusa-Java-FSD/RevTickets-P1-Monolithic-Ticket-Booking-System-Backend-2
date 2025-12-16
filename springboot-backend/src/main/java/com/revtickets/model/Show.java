package com.revtickets.model;

import jakarta.persistence.*;
import java.util.List;
import lombok.Data;

@Entity
@Table(name = "shows")
@Data
public class Show {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private Long movieId;
    private Long eventId;
    private String theater;
    private String showTime;
    private String showDate;
    private Double price;
    private Integer totalSeats;
    private Integer availableSeats;
    
    @ElementCollection
    private List<String> bookedSeats;
    
    public Show() {
        this.bookedSeats = new java.util.ArrayList<>();
    }
}