package com.revtickets.service;

import com.revtickets.model.Booking;
import com.revtickets.model.Event;
import com.revtickets.model.Show;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    
    @Autowired
    private JavaMailSender mailSender;
    
    public void sendOtp(String email, String otp) {
        System.out.println("📧 Sending OTP to: " + email);
        System.out.println("🔑 OTP Code: " + otp + " (for testing)");
        
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("burrasaideep2000@gmail.com");
            message.setTo(email);
            message.setSubject("RevTickets - Your OTP Code");
            message.setText(
                "Hello!\n\n" +
                "Your OTP code is: " + otp + "\n\n" +
                "This code expires in 5 minutes.\n\n" +
                "RevTickets Team"
            );
            
            System.out.println("📤 Attempting to send email via SMTP...");
            mailSender.send(message);
            System.out.println("✅ Email sent successfully to: " + email);
            
        } catch (Exception e) {
            System.err.println("❌ Email error: " + e.getMessage());
            System.err.println("❌ Error class: " + e.getClass().getName());
            e.printStackTrace();
            throw new RuntimeException("Email sending failed: " + e.getMessage());
        }
    }
    
    public void sendPasswordResetEmail(String email, String resetToken) {
        System.out.println("🔐 Sending password reset email to: " + email);
        
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("burrasaideep2000@gmail.com");
            message.setTo(email);
            message.setSubject("RevTickets - Password Reset Request");
            
            String resetUrl = "http://localhost:5173/reset-password?token=" + resetToken;
            message.setText(
                "Hello!\n\n" +
                "You requested to reset your password for your RevTickets account.\n\n" +
                "Click the link below to reset your password:\n" +
                resetUrl + "\n\n" +
                "This link will expire in 1 hour.\n\n" +
                "If you didn't request this, please ignore this email.\n\n" +
                "RevTickets Team"
            );
            
            mailSender.send(message);
            System.out.println("✅ Password reset email sent to: " + email);
            
        } catch (Exception e) {
            System.err.println("❌ Failed to send password reset email: " + e.getMessage());
            throw new RuntimeException("Password reset email sending failed: " + e.getMessage());
        }
    }
    
    public void sendBookingConfirmation(Booking booking, String userEmail) {
        System.out.println("🎫 Sending booking confirmation to: " + userEmail);
        
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("burrasaideep2000@gmail.com");
            message.setTo(userEmail);
            message.setSubject("RevTickets - Booking Confirmation #" + booking.getId());
            message.setText(generateTicketContent(booking));
            
            mailSender.send(message);
            System.out.println("✅ Booking confirmation email sent to: " + userEmail);
        } catch (Exception e) {
            System.err.println("❌ Failed to send booking email: " + e.getMessage());
            // Don't throw exception - booking should succeed even if email fails
        }
    }

    private String generateTicketContent(Booking booking) {
        StringBuilder ticket = new StringBuilder();
        
        ticket.append("═══════════════════════════════════════\n");
        ticket.append("        REVTICKETS E-TICKET\n");
        ticket.append("═══════════════════════════════════════\n\n");
        
        ticket.append("Booking ID: #").append(booking.getId()).append("\n");
        ticket.append("Status: ").append(booking.getStatus()).append("\n");
        ticket.append("Booking Date: ").append(booking.getBookingDate()).append("\n\n");
        
        ticket.append("───────────────────────────────────────\n");
        ticket.append("BOOKING DETAILS\n");
        ticket.append("───────────────────────────────────────\n");
        
        // Event/Show details
        if (booking.getEvent() != null) {
            Event event = booking.getEvent();
            ticket.append("Event: ").append(event.getTitle()).append("\n");
            if (event.getLocation() != null) {
                ticket.append("Location: ").append(event.getLocation()).append("\n");
            }
            if (event.getEventDate() != null) {
                ticket.append("Date: ").append(event.getEventDate()).append("\n");
            }
        }
        
        if (booking.getShow() != null) {
            Show show = booking.getShow();
            ticket.append("Theater: ").append(show.getTheater()).append("\n");
            ticket.append("Show Date: ").append(show.getShowDate()).append("\n");
            ticket.append("Show Time: ").append(show.getShowTime()).append("\n");
        }
        
        ticket.append("\nSeats: ").append(String.join(", ", booking.getSeats())).append("\n");
        ticket.append("Number of Seats: ").append(booking.getSeats().size()).append("\n");
        
        ticket.append("\n───────────────────────────────────────\n");
        ticket.append("PAYMENT DETAILS\n");
        ticket.append("───────────────────────────────────────\n");
        ticket.append("Total Amount: ₹").append(booking.getTotalPrice()).append("\n");
        
        ticket.append("\n═══════════════════════════════════════\n");
        ticket.append("Thank you for booking with RevTickets!\n");
        ticket.append("Please show this e-ticket at the venue.\n");
        ticket.append("═══════════════════════════════════════\n");
        
        return ticket.toString();
    }
}