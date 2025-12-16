package com.revtickets.service;

import com.revtickets.model.OtpVerification;
import com.revtickets.repository.OtpVerificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Random;
import java.util.Optional;

@Service
public class OtpService {
    
    @Autowired
    private OtpVerificationRepository otpRepository;
    
    @Autowired
    private EmailService emailService;
    
    public String generateAndSendOtp(String email) {
        try {
            // Delete any existing OTP for this email
            otpRepository.deleteByEmail(email);
            
            // Generate 6-digit OTP
            String otp = String.format("%06d", new Random().nextInt(999999));
            
            // Save OTP to database
            OtpVerification otpVerification = new OtpVerification(email, otp);
            otpRepository.save(otpVerification);
            
            // Send OTP via email (with fallback)
            try {
                emailService.sendOtp(email, otp);
            } catch (Exception e) {
                System.err.println("Email sending failed, but OTP saved: " + e.getMessage());
            }
            
            System.out.println("OTP generated: " + otp + " for email: " + email);
            
            return "OTP sent successfully to " + email;
        } catch (Exception e) {
            System.err.println("Error in generateAndSendOtp: " + e.getMessage());
            throw new RuntimeException("Failed to generate OTP", e);
        }
    }
    
    public boolean verifyOtp(String email, String otp) {
        System.out.println("Verifying OTP for email: " + email + ", OTP: " + otp);

        // Removed hardcoded OTP bypass for security
        
        // Find the latest OTP for this email
        Optional<OtpVerification> otpVerificationOpt = 
            otpRepository.findTopByEmailOrderByCreatedAtDesc(email);
        
        if (otpVerificationOpt.isPresent()) {
            OtpVerification otpVerification = otpVerificationOpt.get();
            
            System.out.println("Found OTP record: " + otpVerification.getOtp() + ", Verified: " + otpVerification.isVerified() + ", Expired: " + otpVerification.isExpired());
            
            // Check if OTP matches, not already verified, and not expired
            if (otpVerification.getOtp().equals(otp) && 
                !otpVerification.isVerified() && 
                !otpVerification.isExpired()) {
                
                otpVerification.setVerified(true);
                otpRepository.save(otpVerification);
                System.out.println("OTP verified successfully");
                return true;
            } else {
                System.out.println("OTP verification failed - mismatch, already used, or expired");
            }
        } else {
            System.out.println("No OTP record found for email: " + email);
        }
        return false;
    }
}