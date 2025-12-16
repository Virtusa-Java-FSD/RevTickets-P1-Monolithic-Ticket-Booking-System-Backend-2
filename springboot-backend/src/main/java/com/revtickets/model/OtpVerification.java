package com.revtickets.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;
import lombok.Data;

@Document(collection = "otp_verifications")
@Data
public class OtpVerification {
    @Id
    private String id;
    
    private String email;
    private String otp;
    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;
    private boolean verified;
    
    public OtpVerification() {
        this.createdAt = LocalDateTime.now();
        this.expiresAt = LocalDateTime.now().plusMinutes(5); // 5 minutes expiry
        this.verified = false;
    }
    
    public OtpVerification(String email, String otp) {
        this();
        this.email = email;
        this.otp = otp;
    }
    
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiresAt);
    }
}