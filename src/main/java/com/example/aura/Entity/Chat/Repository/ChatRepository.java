package com.example.aura.Entity.Chat.Repository;

import com.example.aura.Entity.Chat.Domain.Chat;
import com.example.aura.Entity.Chat.Domain.ChatStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatRepository extends JpaRepository<Chat, Long> {
    List<Chat> findByStatus(ChatStatus status);
    List<Chat> findByReservationId(Long reservationId);
    List<Chat> findByTicketId(Long ticketId);
}
