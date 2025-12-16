package com.revtickets.controller;

import com.revtickets.dto.AuthRequest;
import com.revtickets.dto.AuthResponse;
import com.revtickets.dto.PasswordResetRequest;
import com.revtickets.dto.NewPasswordRequest;
import com.revtickets.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.Map;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:5173")
@Tag(name = "Authentication", description = "APIs for user authentication and registration")
public class AuthController {
    
    @Autowired
    private UserService userService;
    
    @Operation(summary = "Register a new user", description = "Creates a new user account and returns authentication token")
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody AuthRequest request) {
        try {
            AuthResponse response = userService.register(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @Operation(summary = "User login", description = "Authenticates user and returns JWT token with user role")
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest request) {
        AuthResponse response = userService.login(request);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@Valid @RequestBody PasswordResetRequest request) {
        try {
            userService.initiatePasswordReset(request.getEmail());
            return ResponseEntity.ok(Map.of("message", "Password reset email sent successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@Valid @RequestBody NewPasswordRequest request) {
        try {
            userService.resetPassword(request.getToken(), request.getNewPassword());
            return ResponseEntity.ok(Map.of("message", "Password reset successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @GetMapping("/validate-reset-token")
    public ResponseEntity<?> validateResetToken(@RequestParam String token) {
        boolean isValid = userService.validateResetToken(token);
        if (isValid) {
            return ResponseEntity.ok(Map.of("valid", true, "message", "Token is valid"));
        } else {
            return ResponseEntity.badRequest().body(Map.of("valid", false, "error", "Invalid or expired token"));
        }
    }
    
    // Endpoint to make a user admin (for initial setup - should be removed or secured in production)
    @PostMapping("/make-admin")
    public ResponseEntity<?> makeUserAdmin(@RequestParam String email) {
        try {
            userService.makeUserAdmin(email);
            return ResponseEntity.ok(Map.of("message", "User " + email + " is now an admin"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @Operation(summary = "Google Sign-In", description = "Authenticates user with Google OAuth token")
    @PostMapping("/google")
    public ResponseEntity<?> googleSignIn(@RequestBody Map<String, String> request) {
        try {
            String idToken = request.get("idToken");
            if (idToken == null || idToken.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Google ID token is required"));
            }
            AuthResponse response = userService.googleSignIn(idToken);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}