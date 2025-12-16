package com.revtickets.repository;

import com.revtickets.model.OtpVerification;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface OtpVerificationRepository extends MongoRepository<OtpVerification, String> {
    Optional<OtpVerification> findByEmailAndOtpAndVerifiedFalse(String email, String otp);
    Optional<OtpVerification> findTopByEmailOrderByCreatedAtDesc(String email);
    void deleteByEmail(String email);
}