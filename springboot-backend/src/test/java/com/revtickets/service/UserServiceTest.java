package com.revtickets.service;

import com.revtickets.dto.AuthRequest;
import com.revtickets.dto.AuthResponse;
import com.revtickets.exception.UserAlreadyExistsException;
import com.revtickets.model.User;
import com.revtickets.repository.UserRepository;
import com.revtickets.security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private UserService userService;

    private AuthRequest authRequest;
    private User user;

    @BeforeEach
    void setUp() {
        authRequest = new AuthRequest();
        authRequest.setEmail("test@example.com");
        authRequest.setPassword("password123");
        authRequest.setName("Test User");
        authRequest.setPhone("1234567890");

        user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");
        user.setName("Test User");
        user.setPhone("1234567890");
        user.setPassword("encodedPassword");
        user.setRole(User.UserRole.USER);
    }

    @Test
    void testRegister_Success() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(jwtUtil.generateToken(anyString())).thenReturn("jwt-token");

        AuthResponse response = userService.register(authRequest);

        assertNotNull(response);
        assertEquals("jwt-token", response.getToken());
        assertNotNull(response.getUser());
        assertEquals("test@example.com", response.getUser().getEmail());
        assertEquals("Test User", response.getUser().getName());

        verify(userRepository, times(1)).findByEmail(authRequest.getEmail());
        verify(passwordEncoder, times(1)).encode(authRequest.getPassword());
        verify(userRepository, times(1)).save(any(User.class));
        verify(jwtUtil, times(1)).generateToken(authRequest.getEmail());
    }

    @Test
    void testRegister_UserAlreadyExists() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));

        assertThrows(UserAlreadyExistsException.class, () -> {
            userService.register(authRequest);
        });

        verify(userRepository, times(1)).findByEmail(authRequest.getEmail());
        verify(userRepository, never()).save(any(User.class));
        verify(jwtUtil, never()).generateToken(anyString());
    }

    @Test
    void testRegister_AdminRole_WhenEmailContainsAdmin() {
        authRequest.setEmail("admin@example.com");
        user.setEmail("admin@example.com");
        user.setRole(User.UserRole.ADMIN);

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User savedUser = invocation.getArgument(0);
            savedUser.setId(1L);
            return savedUser;
        });
        when(jwtUtil.generateToken(anyString())).thenReturn("jwt-token");

        AuthResponse response = userService.register(authRequest);

        assertNotNull(response);
        verify(userRepository, times(1)).save(argThat(u -> 
            u.getEmail().equals("admin@example.com") && 
            u.getRole() == User.UserRole.ADMIN
        ));
    }
}

