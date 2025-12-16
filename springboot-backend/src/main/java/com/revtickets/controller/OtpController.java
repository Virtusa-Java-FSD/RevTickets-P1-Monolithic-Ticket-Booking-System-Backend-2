package com.revtickets.controller;

import com.revtickets.service.OtpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/otp")
@CrossOrigin(origins = "http://localhost:5173")
public class OtpController {
    
    @Autowired
    private OtpService otpService;
    
    @PostMapping("/send")
    public ResponseEntity<?> sendOtp(@RequestBody Map<String, String> request) {
        try {
            String email = request.get("email");
            if (email == null || email.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Email is required"));
            }
            String message = otpService.generateAndSendOtp(email);
            return ResponseEntity.ok(Map.of("message", message, "success", true));
        } catch (Exception e) {
            System.err.println("Error sending OTP: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("error", "Failed to send OTP: " + e.getMessage()));
        }
    }
    
    @PostMapping("/verify")
    public ResponseEntity<?> verifyOtp(@RequestBody Map<String, String> request) {
        try {
            String email = request.get("email");
            String otp = request.get("otp");
            
            if (email == null || otp == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Email and OTP are required", "verified", false));
            }
            
            System.out.println("Verify request - Email: " + email + ", OTP: " + otp);
            
            boolean isValid = otpService.verifyOtp(email, otp);
            
            if (isValid) {
                return ResponseEntity.ok(Map.of("message", "OTP verified successfully", "verified", true));
            } else {
                return ResponseEntity.badRequest().body(Map.of("error", "Invalid or expired OTP", "verified", false));
            }
        } catch (Exception e) {
            System.err.println("Error verifying OTP: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("error", "OTP verification failed", "verified", false));
        }
    }
}