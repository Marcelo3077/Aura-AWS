package com.example.aura.Entity.Review.Domain;

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
@Table(name = "review")
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_id")
    private Long id;

    @Column(name = "comment")
    private String comment;

    @Column(name = "rating")
    private Integer rating;

     @Column(name = "created_at")
    private LocalDate createdAt;

    @Column(name = "updated_at")
    private LocalDate updatedAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20, nullable = false)
    private ReviewStatus status;


    @OneToOne(optional = false)
    @JoinColumn(name = "reservation_id", nullable = false, unique = true)
    private Reservation reservation;
}
