package com.revtickets.model;

import java.util.List;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "events")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String title;
    
    @Column(length = 1000)
    private String description;
    
    private String category;
    private String imageUrl;
    private Double rating;
    
    @ElementCollection
    private List<String> genres;
    
    private Double price;
    private String genre;
    private Integer duration;
    private String releaseDate;
    private String language;
    private String format;
    private String location;
    private String eventDate;
    private Integer seats;
    private Integer speakers;
    private Boolean isNewRelease;
    private String industry;
}