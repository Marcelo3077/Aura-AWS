package com.example.aura.Entity.Message.Domain;

import com.example.aura.Entity.Chat.Domain.Chat;
import com.example.aura.Entity.Superuser.Domain.Superuser;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "message")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "message_id")
    private Long id;

    @Column(name = "content", nullable = false, length = 1000)
    private String content;

    @Column(name = "sent_at", nullable = false)
    private LocalDateTime sentAt;

    @Column(name = "type", nullable = false, length = 20)
    private MessageType type;

    @Column(name = "message_status", nullable = false, length = 20)
    private MessageStatus messageStatus;


    @ManyToOne(optional = false)
    @JoinColumn(name = "chat_id", nullable = false)
    private Chat chat;

    @ManyToOne(optional = false)
    @JoinColumn(name = "sender_id", nullable = false)
    private Superuser sender;


    @ManyToOne(optional = false)
    @JoinColumn(name = "receiver_id", nullable = false)
    private Superuser receiver;
}
