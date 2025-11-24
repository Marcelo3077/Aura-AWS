package com.example.aura.Entity.Reservation.Domain;


import com.example.aura.Entity.Chat.Domain.Chat;
import com.example.aura.Entity.Payment.Domain.Payment;
import com.example.aura.Entity.Review.Domain.Review;
import com.example.aura.Entity.SupportTicket.Domain.SupportTicket;
import com.example.aura.Entity.TechnicianService.Domain.TechnicianService;
import com.example.aura.Entity.User.Domain.User;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "reservation")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class Reservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reservation_id")
    private Long id;

    @Column(name = "reservation_date", nullable = false)
    private LocalDate reservationDate;

    @Column(name = "service_date", nullable = false)
    private LocalDate serviceDate;

    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    @Column(name = "end_time", nullable = true)
    private LocalTime endTime;

    // Other fields
    @Column(name = "address", length = 255, nullable = false)
    private String address;

    @Column(name = "status", length = 20, nullable = false)
    private ReservationStatus status;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(optional = false)
    @JoinColumns({
            @JoinColumn(name = "technician_id", referencedColumnName = "technician_id"),
            @JoinColumn(name = "service_id", referencedColumnName = "service_id")
    })
    private TechnicianService technicianService;

    @OneToMany(mappedBy = "reservation", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Payment> payments = new ArrayList<>();


    @OneToOne(mappedBy = "reservation", optional = true, cascade = CascadeType.ALL, orphanRemoval = true)
    private Review review;


    @OneToMany(mappedBy = "reservation", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SupportTicket> tickets = new ArrayList<>();


    @OneToMany(mappedBy = "reservation", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Chat> chats = new ArrayList<>();


    public void addChat(Chat chat) {
        chats.add(chat);
        chat.setReservation(this);
    }

}
