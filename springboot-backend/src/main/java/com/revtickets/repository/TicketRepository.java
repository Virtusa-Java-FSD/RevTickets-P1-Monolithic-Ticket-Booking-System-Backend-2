package com.revtickets.repository;

import com.revtickets.model.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {
    @org.springframework.data.jpa.repository.Query("SELECT t FROM Ticket t WHERE t.user.id = :userId")
    List<Ticket> findByUserId(@org.springframework.data.repository.query.Param("userId") Long userId);
    
    Ticket findByTicketId(String ticketId);
}
