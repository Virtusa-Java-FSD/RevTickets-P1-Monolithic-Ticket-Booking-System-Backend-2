package com.revtickets.controller;

import com.revtickets.model.Payment;
import com.revtickets.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/mock-payment")
@CrossOrigin(origins = "http://localhost:5173")
public class MockPaymentController {

    @Autowired
    private PaymentRepository paymentRepository;

    @PostMapping("/create")
    public ResponseEntity<?> createMockPayment(@RequestBody Map<String, Object> data) {
        try {
            Double amount = ((Number) data.get("amount")).doubleValue();
            
            Payment payment = new Payment();
            payment.setRazorpayOrderId("order_mock_" + System.currentTimeMillis());
            payment.setRazorpayPaymentId("pay_mock_" + System.currentTimeMillis());
            payment.setAmount(amount);
            payment.setCurrency("INR");
            payment.setStatus("SUCCESS");
            payment.setPaymentDate(LocalDateTime.now());
            
            Payment savedPayment = paymentRepository.save(payment);
            
            return ResponseEntity.ok(Map.of(
                "paymentId", savedPayment.getRazorpayPaymentId(),
                "orderId", savedPayment.getRazorpayOrderId(),
                "amount", savedPayment.getAmount(),
                "status", "success"
            ));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                "error", "Mock payment failed: " + e.getMessage()
            ));
        }
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllPayments() {
        return ResponseEntity.ok(paymentRepository.findAll());
    }
}