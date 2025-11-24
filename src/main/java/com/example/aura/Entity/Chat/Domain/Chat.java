package com.example.aura.Entity.Chat.Domain;

import com.example.aura.Entity.Message.Domain.Message;
import com.example.aura.Entity.Reservation.Domain.Reservation;
import com.example.aura.Entity.SupportTicket.Domain.SupportTicket;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name = "chat")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Chat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chat_id")
    private Long id;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private ChatStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "chat_type", nullable = false, length = 50)
    private ChatType chatType;

    @OneToMany(mappedBy = "chat", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Message> messages = new ArrayList<>();

    @ManyToOne(optional = true)
    @JoinColumn(name = "reservation_id")
    private Reservation reservation;

    @ManyToOne(optional = true)
    @JoinColumn(name = "ticket_id")
    private SupportTicket ticket;


    public void addMessage(Message message) {
        messages.add(message);
        message.setChat(this);
    }


}
