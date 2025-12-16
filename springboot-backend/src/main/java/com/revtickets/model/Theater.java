package com.revtickets.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "theaters")
@Data
public class Theater {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String name;
    
    private String location;
    
    private String city;
    
    private String state;
    
    private Integer totalScreens;
    
    private Boolean isActive;
    
    public Theater() {
        this.isActive = true;
    }
    
    public Theater(String name) {
        this.name = name;
        this.isActive = true;
    }
    
    public Theater(String name, String city, String state) {
        this.name = name;
        this.city = city;
        this.state = state;
        this.isActive = true;
    }
}

