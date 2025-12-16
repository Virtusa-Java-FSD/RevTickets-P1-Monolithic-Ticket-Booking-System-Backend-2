package com.revtickets.service;

import com.revtickets.model.Booking;
import com.revtickets.model.Payment;
import com.revtickets.model.Ticket;
import com.revtickets.repository.BookingRepository;
import com.revtickets.repository.PaymentRepository;
import com.revtickets.repository.TicketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DataValidationService {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private TicketRepository ticketRepository;

    public void validateAndLogBookingData(Long bookingId, String paymentId) {
        try {
            // Validate Booking data
            Booking booking = bookingRepository.findById(bookingId).orElse(null);
            if (booking != null) {
                System.out.println("=== BOOKING DATA STORED ===");
                System.out.println("Booking ID: " + booking.getId());
                System.out.println("User ID: " + (booking.getUser() != null ? booking.getUser().getId() : "NULL"));
                System.out.println("Total Price: " + booking.getTotalPrice());
                System.out.println("Seats: " + booking.getSeats());
                System.out.println("Status: " + booking.getStatus());
                System.out.println("Booking Date: " + booking.getBookingDate());
            }

            // Validate Payment data
            if (paymentId != null) {
                Payment payment = paymentRepository.findByRazorpayPaymentId(paymentId).orElse(null);
                if (payment != null) {
                    System.out.println("=== PAYMENT DATA STORED ===");
                    System.out.println("Payment ID: " + payment.getId());
                    System.out.println("Razorpay Payment ID: " + payment.getRazorpayPaymentId());
                    System.out.println("Amount: " + payment.getAmount());
                    System.out.println("Currency: " + payment.getCurrency());
                    System.out.println("Status: " + payment.getStatus());
                    System.out.println("Ticket ID: " + payment.getTicketId());
                    System.out.println("User ID: " + (payment.getUser() != null ? payment.getUser().getId() : "NULL"));
                    System.out.println("Payment Date: " + payment.getPaymentDate());
                } else {
                    System.err.println("PAYMENT DATA NOT FOUND for paymentId: " + paymentId);
                }
            }

            // Validate Ticket data
            if (booking != null) {
                // Find tickets by user ID
                if (booking.getUser() != null) {
                    System.out.println("=== TICKET DATA VALIDATION ===");
                    System.out.println("Checking tickets for user ID: " + booking.getUser().getId());
                    // Note: You might need to add a method to find tickets by user or booking
                }
            }

        } catch (Exception e) {
            System.err.println("Data validation failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
}