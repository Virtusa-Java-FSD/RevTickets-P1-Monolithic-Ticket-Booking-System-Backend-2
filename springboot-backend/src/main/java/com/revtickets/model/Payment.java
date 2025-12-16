package com.revtickets.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "payments")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String razorpayOrderId;
    private String razorpayPaymentId;
    private String razorpaySignature;
    
    private Double amount;
    private String currency; // usually "INR"
    private String status; // Created, Captured, Failed
    
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = true)
    private User user; // Link payment to user
    
    @Column(nullable = true)
    private String ticketId; // Link payment to ticket
    
    private LocalDateTime paymentDate;
    
    // Temporarily removed to avoid mapping issues
    // @OneToOne(mappedBy = "payment")
    // private Booking booking;

    public Payment(String razorpayOrderId, String razorpayPaymentId, String razorpaySignature, Double amount, String status) {
        this.razorpayOrderId = razorpayOrderId;
        this.razorpayPaymentId = razorpayPaymentId;
        this.razorpaySignature = razorpaySignature;
        this.amount = amount;
        this.currency = "INR";
        this.status = status;
        this.paymentDate = LocalDateTime.now();
    }
}
