package com.revtickets.repository;

import com.revtickets.model.Travel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TravelRepository extends JpaRepository<Travel, Long> {
    List<Travel> findByType(String type);
    List<Travel> findByDepartureAndArrival(String departure, String arrival);
    List<Travel> findByTypeAndDepartureAndArrival(String type, String departure, String arrival);
}