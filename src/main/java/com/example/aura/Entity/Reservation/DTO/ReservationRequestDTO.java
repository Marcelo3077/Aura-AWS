package com.example.aura.Entity.Reservation.DTO;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReservationRequestDTO {

    @NotNull(message = "User ID is required")
    private Long userId;

    @NotNull(message = "Technician ID is required")
    private Long technicianId;

    @NotNull(message = "Service ID is required")
    private Long serviceId;

    @NotNull(message = "Service date is required")
    @Future(message = "Service date must be in the future")
    private LocalDate serviceDate;

    @NotNull(message = "Start time is required")
    private LocalTime startTime;

    @NotBlank(message = "Address is required")
    @Size(max = 255, message = "Address must not exceed 255 characters")
    private String address;
}
