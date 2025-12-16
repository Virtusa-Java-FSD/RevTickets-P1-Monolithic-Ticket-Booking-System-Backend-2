package com.revtickets.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "bookings")
@Data
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    
    @ManyToOne
    @JoinColumn(name = "event_id")
    private Event event;

    @ManyToOne
    @JoinColumn(name = "show_id")
    private Show show;

    @ManyToOne
    @JoinColumn(name = "travel_id")
    private Travel travel;
    
    @ElementCollection
    private List<String> seats;
    
    private Double totalPrice;
    private String status;
    private LocalDateTime bookingDate;
    
    @Transient
    private String paymentId; // Capture paymentId from frontend JSON
    
    // Payment relationship temporarily removed to avoid database constraints
    // @OneToOne(cascade = CascadeType.ALL)
    // @JoinColumn(name = "payment_id", referencedColumnName = "id")
    // private Payment payment;
    
    // Temporary getter/setter for payment to maintain compatibility
    @Transient
    private Payment payment;
    
    public Payment getPayment() {
        return payment;
    }
    
    public void setPayment(Payment payment) {
        this.payment = payment;
    }
    
    public Booking() {
        this.bookingDate = LocalDateTime.now();
        this.status = "CONFIRMED";
    }
    
    // Custom setters for transient relationships

    


    // Helper setters for JSON deserialization (Frontend sends IDs, we map to Objects)
    
    public void setUserId(Long userId) {
        if (userId != null) {
            if (this.user == null) this.user = new User();
            this.user.setId(userId);
        }
    }

    public void setEventId(Long eventId) {
        if (eventId != null) {
            if (this.event == null) this.event = new Event();
            this.event.setId(eventId);
        }
    }
    
    public void setShowId(Long showId) {
        if (showId != null) {
            if (this.show == null) this.show = new Show();
            this.show.setId(showId);
        }
    }
    
    public void setTravelId(Long travelId) {
        if (travelId != null) {
            if (this.travel == null) this.travel = new Travel();
            this.travel.setId(travelId);
        }
    }
}