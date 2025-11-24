package com.example.aura.Entity.Reservation.DTO;

import com.example.aura.Entity.Reservation.Domain.ReservationStatus;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReservationUpdateDTO {

    private LocalDate serviceDate;

    private LocalTime startTime;

    private LocalTime endTime;

    @Size(max = 255, message = "Address must not exceed 255 characters")
    private String address;

    private ReservationStatus status;
}
