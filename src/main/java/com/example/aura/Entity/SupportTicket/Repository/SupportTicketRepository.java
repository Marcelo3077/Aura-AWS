package com.example.aura.Entity.SupportTicket.Repository;

import com.example.aura.Entity.SupportTicket.Domain.SupportTicket;
import com.example.aura.Entity.SupportTicket.Domain.TicketPriority;
import com.example.aura.Entity.SupportTicket.Domain.TicketStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SupportTicketRepository extends JpaRepository<SupportTicket, Long> {
    List<SupportTicket> findByStatus(TicketStatus status);
    List<SupportTicket> findByPriority(TicketPriority priority);
    List<SupportTicket> findByAdminId(Long adminId);
    List<SupportTicket> findByReservationId(Long reservationId);
    List<SupportTicket> findByAdminIsNull();
}
