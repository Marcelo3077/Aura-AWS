package com.example.aura.Entity.Payment.Domain;


import com.example.aura.Entity.Reservation.Domain.Reservation;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "payment")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_id")
    private Long id;

    @Column(name = "amount")
    private Double amount;

    @Column(name = "payment_date")
    private LocalDate paymentDate;

    @Column(name = "method")
    private PaymentMethod paymentMethod;

    @Column(name = "status")
    private PaymentStatus paymentStatus;

    @ManyToOne(optional = false)
    @JoinColumn(name = "reservation_id", nullable = false)
    private Reservation reservation;

}
