package com.example.aura.Entity.Chat.Controller;

import com.example.aura.Entity.Chat.Domain.Chat;
import com.example.aura.Entity.Chat.Domain.ChatStatus;
import com.example.aura.Entity.Chat.Domain.ChatType;
import com.example.aura.Entity.Chat.Service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chats")
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
public class ChatController {

    private final ChatService chatService;

    @PostMapping("/reservation/{reservationId}")
    @PreAuthorize("hasAnyRole('USER', 'TECHNICIAN')")
    public ResponseEntity<Chat> createChatForReservation(
            @PathVariable Long reservationId,
            @RequestParam ChatType chatType) {
        Chat response = chatService.createChatForReservation(reservationId, chatType);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/ticket/{ticketId}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'SUPERADMIN')")
    public ResponseEntity<Chat> createChatForTicket(
            @PathVariable Long ticketId,
            @RequestParam ChatType chatType) {
        Chat response = chatService.createChatForTicket(ticketId, chatType);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Chat> getChatById(@PathVariable Long id) {
        Chat response = chatService.getChatById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERADMIN')")
    public ResponseEntity<List<Chat>> getAllChats() {
        List<Chat> response = chatService.getAllChats();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/reservation/{reservationId}")
    public ResponseEntity<List<Chat>> getChatsByReservationId(@PathVariable Long reservationId) {
        List<Chat> response = chatService.getChatsByReservationId(reservationId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/ticket/{ticketId}")
    public ResponseEntity<List<Chat>> getChatsByTicketId(@PathVariable Long ticketId) {
        List<Chat> response = chatService.getChatsByTicketId(ticketId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/status/{status}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERADMIN')")
    public ResponseEntity<List<Chat>> getChatsByStatus(@PathVariable ChatStatus status) {
        List<Chat> response = chatService.getChatsByStatus(status);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<Chat> updateChatStatus(
            @PathVariable Long id,
            @RequestParam ChatStatus status) {
        Chat response = chatService.updateChatStatus(id, status);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/close")
    public ResponseEntity<Chat> closeChat(@PathVariable Long id) {
        Chat response = chatService.closeChat(id);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/archive")
    public ResponseEntity<Chat> archiveChat(@PathVariable Long id) {
        Chat response = chatService.archiveChat(id);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERADMIN')")
    public ResponseEntity<Void> deleteChat(@PathVariable Long id) {
        chatService.deleteChat(id);
        return ResponseEntity.noContent().build();
    }
}
