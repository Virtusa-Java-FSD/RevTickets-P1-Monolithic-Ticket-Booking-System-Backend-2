package com.revtickets.repository;

import com.revtickets.model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    @org.springframework.data.jpa.repository.Query("SELECT b FROM Booking b WHERE b.user.id = :userId")
    List<Booking> findByUserId(@org.springframework.data.repository.query.Param("userId") Long userId);
}