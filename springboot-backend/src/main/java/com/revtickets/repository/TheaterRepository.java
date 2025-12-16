package com.revtickets.repository;

import com.revtickets.model.Theater;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface TheaterRepository extends JpaRepository<Theater, Long> {
    Optional<Theater> findByName(String name);
    List<Theater> findByIsActiveTrue();
    List<Theater> findByCity(String city);
}

