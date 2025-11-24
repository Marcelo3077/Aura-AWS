package com.example.aura.Entity.TechnicianService.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TechnicianServiceResponseDTO {
    private Long technicianId;
    private String technicianName;
    private Long serviceId;
    private String serviceName;
    private Double baseRate;
    private Integer totalReservations;
}
