package com.revtickets.repository;

import com.revtickets.model.MongoPayment;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MongoPaymentRepository extends MongoRepository<MongoPayment, String> {
}