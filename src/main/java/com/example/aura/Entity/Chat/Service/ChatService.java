package com.example.aura.Entity.Chat.Service;

import com.example.aura.Entity.Chat.Domain.Chat;
import com.example.aura.Entity.Chat.Domain.ChatStatus;
import com.example.aura.Entity.Chat.Domain.ChatType;
import com.example.aura.Entity.Chat.Repository.ChatRepository;
import com.example.aura.Entity.Reservation.Domain.Reservation;
import com.example.aura.Entity.Reservation.Repository.ReservationRepository;
import com.example.aura.Entity.SupportTicket.Domain.SupportTicket;
import com.example.aura.Entity.SupportTicket.Repository.SupportTicketRepository;
import com.example.aura.Exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatRepository chatRepository;
    private final ReservationRepository reservationRepository;
    private final SupportTicketRepository supportTicketRepository;

    @Transactional
    public Chat createChatForReservation(Long reservationId, ChatType chatType) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation", "id", reservationId));

        Chat chat = new Chat();
        chat.setReservation(reservation);
        chat.setChatType(chatType);
        chat.setStatus(ChatStatus.OPEN);
        chat.setCreatedAt(LocalDateTime.now());

        return chatRepository.save(chat);
    }

    @Transactional
    public Chat createChatForTicket(Long ticketId, ChatType chatType) {
        SupportTicket ticket = supportTicketRepository.findById(ticketId)
                .orElseThrow(() -> new ResourceNotFoundException("SupportTicket", "id", ticketId));

        Chat chat = new Chat();
        chat.setTicket(ticket);
        chat.setChatType(chatType);
        chat.setStatus(ChatStatus.OPEN);
        chat.setCreatedAt(LocalDateTime.now());

        return chatRepository.save(chat);
    }

    @Transactional(readOnly = true)
    public Chat getChatById(Long id) {
        return chatRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Chat", "id", id));
    }

    @Transactional(readOnly = true)
    public List<Chat> getAllChats() {
        return chatRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Chat> getChatsByReservationId(Long reservationId) {
        return chatRepository.findAll().stream()
                .filter(c -> c.getReservation() != null && c.getReservation().getId().equals(reservationId))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<Chat> getChatsByTicketId(Long ticketId) {
        return chatRepository.findAll().stream()
                .filter(c -> c.getTicket() != null && c.getTicket().getId().equals(ticketId))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<Chat> getChatsByStatus(ChatStatus status) {
        return chatRepository.findAll().stream()
                .filter(c -> c.getStatus() == status)
                .toList();
    }

    @Transactional
    public Chat updateChatStatus(Long id, ChatStatus status) {
        Chat chat = chatRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Chat", "id", id));

        chat.setStatus(status);
        return chatRepository.save(chat);
    }

    @Transactional
    public Chat closeChat(Long id) {
        Chat chat = chatRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Chat", "id", id));

        chat.setStatus(ChatStatus.CLOSED);
        return chatRepository.save(chat);
    }

    @Transactional
    public Chat archiveChat(Long id) {
        Chat chat = chatRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Chat", "id", id));

        chat.setStatus(ChatStatus.ARCHIVED);
        return chatRepository.save(chat);
    }

    @Transactional
    public void deleteChat(Long id) {
        Chat chat = chatRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Chat", "id", id));
        chatRepository.delete(chat);
    }
}
