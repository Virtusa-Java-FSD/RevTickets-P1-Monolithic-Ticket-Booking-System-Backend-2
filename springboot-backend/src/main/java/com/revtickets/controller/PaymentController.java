package com.revtickets.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/payment")
@CrossOrigin(origins = "http://localhost:5173")
public class PaymentController {
    
    @Autowired
    private com.revtickets.repository.PaymentRepository paymentRepository;
    
    @PostMapping("/create-order")
    public ResponseEntity<?> createOrder(@RequestBody Map<String, Object> data) {
        try {
            Integer amount = (Integer) data.get("amount");
            String orderId = "order_" + System.currentTimeMillis();
            
            // Store payment record
            com.revtickets.model.Payment payment = new com.revtickets.model.Payment();
            payment.setRazorpayOrderId(orderId);
            payment.setAmount(amount.doubleValue());
            payment.setCurrency("INR");
            payment.setStatus("CREATED");
            payment.setPaymentDate(LocalDateTime.now());
            
            paymentRepository.save(payment);
            
            return ResponseEntity.ok(Map.of(
                "orderId", orderId,
                "amount", amount * 100,
                "currency", "INR",
                "keyId", "rzp_test_mock"
            ));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Failed to create order: " + e.getMessage()));
        }
    }
    
    @PostMapping("/verify")
    public ResponseEntity<?> verifyPayment(@RequestBody Map<String, String> data) {
        try {
            String orderId = data.get("razorpay_order_id");
            String paymentId = data.get("razorpay_payment_id");
            String signature = data.get("razorpay_signature");
            
            // Mock verification - always success
            com.revtickets.model.Payment payment = paymentRepository.findByRazorpayOrderId(orderId).orElse(null);
            if (payment != null) {
                payment.setRazorpayPaymentId(paymentId);
                payment.setRazorpaySignature(signature);
                payment.setStatus("CAPTURED");
                paymentRepository.save(payment);
                
                return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "message", "Payment verified successfully",
                    "paymentId", paymentId,
                    "orderId", orderId
                ));
            } else {
                return ResponseEntity.badRequest()
                        .body(Map.of("status", "failed", "message", "Order not found"));
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Payment verification failed: " + e.getMessage()));
        }
    }
    
    @GetMapping("/key")
    public ResponseEntity<?> getKey() {
        return ResponseEntity.ok(Map.of("keyId", "rzp_test_mock"));
    }
    
    @PostMapping("/success")
    public ResponseEntity<?> paymentSuccess(@RequestBody Map<String, Object> data) {
        try {
            String paymentId = (String) data.get("paymentId");
            String orderId = (String) data.get("orderId");
            Double amount = ((Number) data.get("amount")).doubleValue();
            
            // Update payment record
            com.revtickets.model.Payment payment = paymentRepository.findByRazorpayPaymentId(paymentId)
                .orElse(paymentRepository.findByRazorpayOrderId(orderId).orElse(null));
                
            if (payment != null) {
                payment.setRazorpayPaymentId(paymentId);
                payment.setAmount(amount);
                payment.setStatus("SUCCESS");
                paymentRepository.save(payment);
                
                return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "message", "Payment recorded successfully"
                ));
            } else {
                return ResponseEntity.badRequest().body(Map.of(
                    "status", "error",
                    "message", "Payment record not found"
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(Map.of(
                "status", "error",
                "message", "Failed to process payment success: " + e.getMessage()
            ));
        }
    }
    
    @GetMapping("/status/{paymentId}")
    public ResponseEntity<?> getPaymentStatus(@PathVariable String paymentId) {
        try {
            com.revtickets.model.Payment payment = paymentRepository.findByRazorpayPaymentId(paymentId).orElse(null);
            
            if (payment != null) {
                Map<String, Object> response = new java.util.HashMap<>();
                response.put("payment", payment);
                response.put("stored", true);
                
                // Check if ticket exists
                if (payment.getTicketId() != null) {
                    response.put("ticketGenerated", true);
                    response.put("ticketId", payment.getTicketId());
                } else {
                    response.put("ticketGenerated", false);
                }
                
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.ok(Map.of(
                    "stored", false,
                    "message", "Payment not found in database"
                ));
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                "error", "Failed to check payment status: " + e.getMessage()
            ));
        }
    }
    
    @GetMapping("/all")
    public ResponseEntity<?> getAllPayments() {
        try {
            java.util.List<com.revtickets.model.Payment> payments = paymentRepository.findAll();
            return ResponseEntity.ok(Map.of(
                "payments", payments,
                "count", payments.size()
            ));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                "error", "Failed to fetch payments: " + e.getMessage()
            ));
        }
    }
}
