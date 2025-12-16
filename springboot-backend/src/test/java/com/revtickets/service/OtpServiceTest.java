package com.revtickets.service;

import com.revtickets.model.OtpVerification;
import com.revtickets.repository.OtpVerificationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class
OtpServiceTest {

    @Mock
    private OtpVerificationRepository otpRepository;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private OtpService otpService;

    private String testEmail;
    private OtpVerification otpVerification;

    @BeforeEach
    void setUp() {
        testEmail = "test@example.com";
        
        otpVerification = new OtpVerification();
        otpVerification.setId("otp-id-123");
        otpVerification.setEmail(testEmail);
        otpVerification.setOtp("123456");
        otpVerification.setCreatedAt(LocalDateTime.now());
        otpVerification.setExpiresAt(LocalDateTime.now().plusMinutes(5));
        otpVerification.setVerified(false);
    }

    @Test
    void testGenerateAndSendOtp_Success() {
        doNothing().when(otpRepository).deleteByEmail(anyString());
        when(otpRepository.save(any(OtpVerification.class))).thenAnswer(invocation -> {
            OtpVerification otp = invocation.getArgument(0);
            otp.setId("generated-id");
            return otp;
        });
        doNothing().when(emailService).sendOtp(anyString(), anyString());

        String result = otpService.generateAndSendOtp(testEmail);

        assertNotNull(result);
        assertTrue(result.contains("OTP sent successfully"));
        assertTrue(result.contains(testEmail));

        verify(otpRepository, times(1)).deleteByEmail(testEmail);
        verify(otpRepository, times(1)).save(any(OtpVerification.class));
        verify(emailService, times(1)).sendOtp(eq(testEmail), anyString());
    }

    @Test
    void testGenerateAndSendOtp_EmailSendingFails_ButOtpSaved() {
        doNothing().when(otpRepository).deleteByEmail(anyString());
        when(otpRepository.save(any(OtpVerification.class))).thenAnswer(invocation -> {
            OtpVerification otp = invocation.getArgument(0);
            otp.setId("generated-id");
            return otp;
        });
        doThrow(new RuntimeException("Email service unavailable"))
            .when(emailService).sendOtp(anyString(), anyString());

        String result = otpService.generateAndSendOtp(testEmail);

        assertNotNull(result);
        assertTrue(result.contains("OTP sent successfully"));

        verify(otpRepository, times(1)).save(any(OtpVerification.class));
        verify(emailService, times(1)).sendOtp(anyString(), anyString());
    }

    @Test
    void testVerifyOtp_Success() {
        when(otpRepository.findTopByEmailOrderByCreatedAtDesc(anyString()))
            .thenReturn(Optional.of(otpVerification));
        when(otpRepository.save(any(OtpVerification.class))).thenReturn(otpVerification);

        boolean result = otpService.verifyOtp(testEmail, "123456");

        assertTrue(result);
        assertTrue(otpVerification.isVerified());

        verify(otpRepository, times(1)).findTopByEmailOrderByCreatedAtDesc(testEmail);
        verify(otpRepository, times(1)).save(otpVerification);
    }

    @Test
    void testVerifyOtp_InvalidOtp() {
        when(otpRepository.findTopByEmailOrderByCreatedAtDesc(anyString()))
            .thenReturn(Optional.of(otpVerification));

        boolean result = otpService.verifyOtp(testEmail, "999999");

        assertFalse(result);
        assertFalse(otpVerification.isVerified());

        verify(otpRepository, times(1)).findTopByEmailOrderByCreatedAtDesc(testEmail);
        verify(otpRepository, never()).save(any(OtpVerification.class));
    }

    @Test
    void testVerifyOtp_ExpiredOtp() {
        otpVerification.setExpiresAt(LocalDateTime.now().minusMinutes(1));

        when(otpRepository.findTopByEmailOrderByCreatedAtDesc(anyString()))
            .thenReturn(Optional.of(otpVerification));

        boolean result = otpService.verifyOtp(testEmail, "123456");

        assertFalse(result);

        verify(otpRepository, times(1)).findTopByEmailOrderByCreatedAtDesc(testEmail);
        verify(otpRepository, never()).save(any(OtpVerification.class));
    }

    @Test
    void testVerifyOtp_OtpNotFound() {
        when(otpRepository.findTopByEmailOrderByCreatedAtDesc(anyString()))
            .thenReturn(Optional.empty());

        boolean result = otpService.verifyOtp(testEmail, "123456");

        assertFalse(result);

        verify(otpRepository, times(1)).findTopByEmailOrderByCreatedAtDesc(testEmail);
        verify(otpRepository, never()).save(any(OtpVerification.class));
    }
}

