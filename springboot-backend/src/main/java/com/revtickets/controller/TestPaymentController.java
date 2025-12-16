package com.revtickets.controller;

import com.revtickets.model.MongoPayment;
import com.revtickets.repository.MongoPaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/test-payment")
@CrossOrigin(origins = "http://localhost:5173")
public class TestPaymentController {

    @Autowired
    private MongoPaymentRepository mongoPaymentRepository;

    @PostMapping("/create")
    public ResponseEntity<?> createPayment(@RequestBody Map<String, Object> data) {
        try {
            Integer amount = (Integer) data.get("amount");
            
            MongoPayment payment = new MongoPayment();
            payment.setOrderId("order_" + System.currentTimeMillis());
            payment.setPaymentId("pay_" + System.currentTimeMillis());
            payment.setAmount(amount.doubleValue());
            payment.setStatus("SUCCESS");
            payment.setTimestamp(LocalDateTime.now());
            
            MongoPayment saved = mongoPaymentRepository.save(payment);
            
            return ResponseEntity.ok(saved);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllPayments() {
        var payments = mongoPaymentRepository.findAll();
        return ResponseEntity.ok(Map.of("payments", payments, "count", payments.size()));
    }

    @PostMapping("/booking")
    public ResponseEntity<?> createBooking(@RequestBody Map<String, Object> data) {
        try {
            String paymentId = (String) data.get("paymentId");
            
            Map<String, Object> booking = new HashMap<>();
            booking.put("id", System.currentTimeMillis());
            booking.put("paymentId", paymentId);
            booking.put("seats", data.get("seats"));
            booking.put("totalPrice", data.get("totalPrice"));
            booking.put("status", "CONFIRMED");
            
            return ResponseEntity.ok(booking);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }
}