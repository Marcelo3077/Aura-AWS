package com.example.aura.Entity.Message.Service;

import com.example.aura.Entity.Chat.Domain.Chat;
import com.example.aura.Entity.Chat.Repository.ChatRepository;
import com.example.aura.Entity.Message.Domain.Message;
import com.example.aura.Entity.Message.Domain.MessageStatus;
import com.example.aura.Entity.Message.Domain.MessageType;
import com.example.aura.Entity.Message.Repository.MessageRepository;
import com.example.aura.Entity.Superuser.Domain.Superuser;
import com.example.aura.Entity.Superuser.Repository.SuperuserRepository;
import com.example.aura.Exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageRepository messageRepository;
    private final ChatRepository chatRepository;
    private final SuperuserRepository<Superuser> superuserRepository;

    @Transactional
    public Message sendMessage(Long chatId, Long senderId, Long receiverId, String content, MessageType type) {
        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new ResourceNotFoundException("Chat", "id", chatId));

        Superuser sender = superuserRepository.findById(senderId)
                .orElseThrow(() -> new ResourceNotFoundException("Superuser", "id", senderId));

        Superuser receiver = superuserRepository.findById(receiverId)
                .orElseThrow(() -> new ResourceNotFoundException("Superuser", "id", receiverId));

        Message message = new Message();
        message.setChat(chat);
        message.setSender(sender);
        message.setReceiver(receiver);
        message.setContent(content);
        message.setType(type);
        message.setSentAt(LocalDateTime.now());
        message.setMessageStatus(MessageStatus.SENT);

        return messageRepository.save(message);
    }

    @Transactional(readOnly = true)
    public Message getMessageById(Long id) {
        return messageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Message", "id", id));
    }

    @Transactional(readOnly = true)
    public List<Message> getMessagesByChatId(Long chatId) {
        return messageRepository.findAll().stream()
                .filter(m -> m.getChat().getId().equals(chatId))
                .sorted((m1, m2) -> m1.getSentAt().compareTo(m2.getSentAt()))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<Message> getMessagesBySenderId(Long senderId) {
        return messageRepository.findAll().stream()
                .filter(m -> m.getSender().getId().equals(senderId))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<Message> getMessagesByReceiverId(Long receiverId) {
        return messageRepository.findAll().stream()
                .filter(m -> m.getReceiver().getId().equals(receiverId))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<Message> getUnreadMessagesByReceiverId(Long receiverId) {
        return messageRepository.findAll().stream()
                .filter(m -> m.getReceiver().getId().equals(receiverId) &&
                        m.getMessageStatus() != MessageStatus.READ)
                .toList();
    }

    @Transactional
    public Message markAsDelivered(Long messageId) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new ResourceNotFoundException("Message", "id", messageId));

        message.setMessageStatus(MessageStatus.DELIVERED);
        return messageRepository.save(message);
    }

    @Transactional
    public Message markAsRead(Long messageId) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new ResourceNotFoundException("Message", "id", messageId));

        message.setMessageStatus(MessageStatus.READ);
        return messageRepository.save(message);
    }

    @Transactional
    public void markAllChatMessagesAsRead(Long chatId, Long receiverId) {
        List<Message> messages = messageRepository.findAll().stream()
                .filter(m -> m.getChat().getId().equals(chatId) &&
                        m.getReceiver().getId().equals(receiverId) &&
                        m.getMessageStatus() != MessageStatus.READ)
                .toList();

        messages.forEach(m -> m.setMessageStatus(MessageStatus.READ));
        messageRepository.saveAll(messages);
    }

    @Transactional
    public void deleteMessage(Long id) {
        Message message = messageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Message", "id", id));
        messageRepository.delete(message);
    }
}
