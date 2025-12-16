package com.revtickets.repository;

import com.revtickets.model.Notification;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface NotificationRepository extends MongoRepository<Notification, String> {
    List<Notification> findByUserId(Long userId);
    List<Notification> findByUserIdAndReadFalse(Long userId);
}
