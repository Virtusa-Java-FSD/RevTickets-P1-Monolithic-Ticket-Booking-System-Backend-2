package com.revtickets.service;

import com.revtickets.dto.AuthRequest;
import com.revtickets.dto.AuthResponse;
import com.revtickets.dto.UserDto;
import com.revtickets.exception.UserAlreadyExistsException;
import com.revtickets.exception.InvalidCredentialsException;
import com.revtickets.model.User;
import com.revtickets.model.PasswordResetToken;
import com.revtickets.repository.UserRepository;
import com.revtickets.repository.PasswordResetTokenRepository;
import com.revtickets.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService implements UserDetailsService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @Autowired
    private PasswordResetTokenRepository passwordResetTokenRepository;
    
    @Autowired
    private EmailService emailService;
    
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
        
        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                new ArrayList<>()
        );
    }
    
    public AuthResponse register(AuthRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new UserAlreadyExistsException("Email already exists");
        }
        
        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        userRepository.save(user);
        
        String token = jwtUtil.generateToken(request.getEmail());
        UserDto userDto = new UserDto(user.getId(), user.getName(), user.getEmail());
        
        return new AuthResponse(token, userDto);
    }
    
    public AuthResponse login(AuthRequest request) {
        Optional<User> userOpt = userRepository.findByEmail(request.getEmail());
        if (userOpt.isEmpty() || !passwordEncoder.matches(request.getPassword(), userOpt.get().getPassword())) {
            throw new InvalidCredentialsException("Invalid credentials");
        }
        
        User user = userOpt.get();
        String token = jwtUtil.generateToken(request.getEmail());
        UserDto userDto = new UserDto(user.getId(), user.getName(), user.getEmail());
        
        return new AuthResponse(token, userDto);
    }
    
    @Transactional
    public void initiatePasswordReset(String email) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            throw new UsernameNotFoundException("User not found with email: " + email);
        }
        
        // Delete any existing reset tokens for this email
        passwordResetTokenRepository.deleteByEmail(email);
        
        // Generate new reset token
        String token = UUID.randomUUID().toString();
        LocalDateTime expiryDate = LocalDateTime.now().plusHours(1); // 1 hour expiry
        
        PasswordResetToken resetToken = new PasswordResetToken(token, email, expiryDate);
        passwordResetTokenRepository.save(resetToken);
        
        // Send reset email
        emailService.sendPasswordResetEmail(email, token);
    }
    
    @Transactional
    public void resetPassword(String token, String newPassword) {
        Optional<PasswordResetToken> tokenOpt = passwordResetTokenRepository.findByToken(token);
        
        if (tokenOpt.isEmpty()) {
            throw new IllegalArgumentException("Invalid reset token");
        }
        
        PasswordResetToken resetToken = tokenOpt.get();
        
        if (resetToken.isUsed()) {
            throw new IllegalArgumentException("Reset token has already been used");
        }
        
        if (resetToken.isExpired()) {
            throw new IllegalArgumentException("Reset token has expired");
        }
        
        // Find user and update password
        Optional<User> userOpt = userRepository.findByEmail(resetToken.getEmail());
        if (userOpt.isEmpty()) {
            throw new UsernameNotFoundException("User not found");
        }
        
        User user = userOpt.get();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        
        // Mark token as used
        resetToken.setUsed(true);
        passwordResetTokenRepository.save(resetToken);
    }
    
    public boolean validateResetToken(String token) {
        Optional<PasswordResetToken> tokenOpt = passwordResetTokenRepository.findByToken(token);
        
        if (tokenOpt.isEmpty()) {
            return false;
        }
        
        PasswordResetToken resetToken = tokenOpt.get();
        return !resetToken.isUsed() && !resetToken.isExpired();
    }
}