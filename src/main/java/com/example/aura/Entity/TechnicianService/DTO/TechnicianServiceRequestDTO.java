package com.example.aura.Entity.TechnicianService.DTO;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TechnicianServiceRequestDTO {

    @NotNull(message = "Technician ID is required")
    private Long technicianId;

    @NotNull(message = "Service ID is required")
    private Long serviceId;

    @NotNull(message = "Base rate is required")
    @Positive(message = "Base rate must be positive")
    private Double baseRate;
}
