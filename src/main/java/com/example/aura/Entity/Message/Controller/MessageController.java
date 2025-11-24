package com.example.aura.Entity.Message.Controller;

import com.example.aura.Entity.Message.Domain.Message;
import com.example.aura.Entity.Message.Domain.MessageType;
import com.example.aura.Entity.Message.Service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
public class MessageController {

    private final MessageService messageService;

    @PostMapping
    public ResponseEntity<Message> sendMessage(
            @RequestParam Long chatId,
            @RequestParam Long senderId,
            @RequestParam Long receiverId,
            @RequestParam String content,
            @RequestParam(defaultValue = "TEXT") MessageType type) {
        Message response = messageService.sendMessage(chatId, senderId, receiverId, content, type);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Message> getMessageById(@PathVariable Long id) {
        Message response = messageService.getMessageById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/chat/{chatId}")
    public ResponseEntity<List<Message>> getMessagesByChatId(@PathVariable Long chatId) {
        List<Message> response = messageService.getMessagesByChatId(chatId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/sender/{senderId}")
    public ResponseEntity<List<Message>> getMessagesBySenderId(@PathVariable Long senderId) {
        List<Message> response = messageService.getMessagesBySenderId(senderId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/receiver/{receiverId}")
    public ResponseEntity<List<Message>> getMessagesByReceiverId(@PathVariable Long receiverId) {
        List<Message> response = messageService.getMessagesByReceiverId(receiverId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/receiver/{receiverId}/unread")
    public ResponseEntity<List<Message>> getUnreadMessagesByReceiverId(@PathVariable Long receiverId) {
        List<Message> response = messageService.getUnreadMessagesByReceiverId(receiverId);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/delivered")
    public ResponseEntity<Message> markAsDelivered(@PathVariable Long id) {
        Message response = messageService.markAsDelivered(id);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/read")
    public ResponseEntity<Message> markAsRead(@PathVariable Long id) {
        Message response = messageService.markAsRead(id);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/chat/{chatId}/read-all")
    public ResponseEntity<Void> markAllChatMessagesAsRead(
            @PathVariable Long chatId,
            @RequestParam Long receiverId) {
        messageService.markAllChatMessagesAsRead(chatId, receiverId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMessage(@PathVariable Long id) {
        messageService.deleteMessage(id);
        return ResponseEntity.noContent().build();
    }
}
