package com.example.aura.Entity.SupportTicket.Domain;

import com.example.aura.Entity.Admin.Domain.Admin;
import com.example.aura.Entity.Payment.Domain.Payment;
import com.example.aura.Entity.Reservation.Domain.Reservation;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "support_ticket")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class SupportTicket {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ticket_id")
    private Long id;

    @Column(name = "subject", length = 100, nullable = false)
    private String subject;

    @Column(name = "description", length = 300)
    private String description;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20, nullable = false)
    private TicketStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "priority", length = 20, nullable = false)
    private TicketPriority priority;

    @Enumerated(EnumType.STRING)
    @Column(name = "category", length = 30, nullable = false)
    private TicketCategory category;



    @ManyToOne(optional = false)
    @JoinColumn(name = "reservation_id", nullable = false)
    private Reservation reservation;

    @ManyToOne(optional = true)
    @JoinColumn(name = "admin_id")
    private Admin admin;

    @ManyToOne(optional = true)
    @JoinColumn(name = "payment_id")
    private Payment payment;
}
