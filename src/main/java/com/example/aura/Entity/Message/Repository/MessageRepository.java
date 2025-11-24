package com.example.aura.Entity.Message.Repository;

import com.example.aura.Entity.Message.Domain.Message;
import com.example.aura.Entity.Message.Domain.MessageStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findByChatIdOrderBySentAtAsc(Long chatId);
    List<Message> findBySenderIdOrderBySentAtDesc(Long senderId);
    List<Message> findByReceiverIdOrderBySentAtDesc(Long receiverId);
    List<Message> findByReceiverIdAndMessageStatus(Long receiverId, MessageStatus status);
}
