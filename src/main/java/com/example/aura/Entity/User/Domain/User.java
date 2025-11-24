package com.example.aura.Entity.User.Domain;

import com.example.aura.Entity.Reservation.Domain.Reservation;
import com.example.aura.Entity.Superuser.Domain.Superuser;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "user_app")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@PrimaryKeyJoinColumn(name = "user_id")
public class User extends Superuser {

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Reservation> reservations = new ArrayList<>();
}