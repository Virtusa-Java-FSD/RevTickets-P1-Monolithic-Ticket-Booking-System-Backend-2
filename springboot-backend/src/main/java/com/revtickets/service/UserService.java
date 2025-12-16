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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
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
    
    @Value("${google.oauth.client-id:}")
    private String googleClientId;
    
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
        
        List<GrantedAuthority> authorities = new ArrayList<>();
        if (user.getRole() != null) {
            authorities.add(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()));
        } else {
            authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        }
        
        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                authorities
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
        if (request.getEmail().toLowerCase().contains("admin")) {
            user.setRole(User.UserRole.ADMIN);
        }
        userRepository.save(user);
        
        String token = jwtUtil.generateToken(request.getEmail());
        UserDto userDto = new UserDto(user.getId(), user.getName(), user.getEmail(), 
                                      user.getRole() != null ? user.getRole().name() : "USER");
        
        return new AuthResponse(token, userDto);
    }
    
    public AuthResponse login(AuthRequest request) {
        Optional<User> userOpt = userRepository.findByEmail(request.getEmail());
        if (userOpt.isEmpty() || !passwordEncoder.matches(request.getPassword(), userOpt.get().getPassword())) {
            throw new InvalidCredentialsException("Invalid credentials");
        }
        
        User user = userOpt.get();
        String token = jwtUtil.generateToken(request.getEmail());
        UserDto userDto = new UserDto(user.getId(), user.getName(), user.getEmail(),
                                      user.getRole() != null ? user.getRole().name() : "USER");
        
        return new AuthResponse(token, userDto);
    }
    
    @Transactional
    public void initiatePasswordReset(String email) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            throw new UsernameNotFoundException("User not found with email: " + email);
        }
        
        passwordResetTokenRepository.deleteByEmail(email);
        
        String token = UUID.randomUUID().toString();
        LocalDateTime expiryDate = LocalDateTime.now().plusHours(1);
        
        PasswordResetToken resetToken = new PasswordResetToken(token, email, expiryDate);
        passwordResetTokenRepository.save(resetToken);
        
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
        
        Optional<User> userOpt = userRepository.findByEmail(resetToken.getEmail());
        if (userOpt.isEmpty()) {
            throw new UsernameNotFoundException("User not found");
        }
        
        User user = userOpt.get();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        
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
    
    public void makeUserAdmin(String email) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setRole(User.UserRole.ADMIN);
            userRepository.save(user);
        } else {
            throw new UsernameNotFoundException("User not found with email: " + email);
        }
    }
    
    public AuthResponse googleSignIn(String idToken) {
        try {
            com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier verifier = 
                new com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier.Builder(
                    new com.google.api.client.http.javanet.NetHttpTransport(),
                    new com.google.api.client.json.gson.GsonFactory())
                .setAudience(java.util.Collections.singletonList(googleClientId != null && !googleClientId.isEmpty() ? googleClientId : "YOUR_GOOGLE_CLIENT_ID"))
                .build();
            
            com.google.api.client.googleapis.auth.oauth2.GoogleIdToken token = verifier.verify(idToken);
            if (token != null) {
                com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload payload = token.getPayload();
                String email = (String) payload.get("email");
                String name = (String) payload.get("name");
                
                if (email == null || email.isEmpty()) {
                    throw new InvalidCredentialsException("Email not found in Google token");
                }
                
                Optional<User> userOpt = userRepository.findByEmail(email);
                User user;
                
                if (userOpt.isPresent()) {
                    user = userOpt.get();
                } else {
                    user = new User();
                    user.setEmail(email);
                    user.setName(name != null && !name.isEmpty() ? name : email.split("@")[0]);
                    user.setPhone("");
                    user.setPassword(passwordEncoder.encode(UUID.randomUUID().toString()));
                    if (email.toLowerCase().contains("admin")) {
                        user.setRole(User.UserRole.ADMIN);
                    }
                    userRepository.save(user);
                }
                
                String jwtToken = jwtUtil.generateToken(email);
                UserDto userDto = new UserDto(user.getId(), user.getName(), user.getEmail(),
                                              user.getRole() != null ? user.getRole().name() : "USER");
                
                return new AuthResponse(jwtToken, userDto);
            } else {
                throw new InvalidCredentialsException("Invalid Google token");
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new InvalidCredentialsException("Google authentication failed: " + e.getMessage());
        }
    }
}