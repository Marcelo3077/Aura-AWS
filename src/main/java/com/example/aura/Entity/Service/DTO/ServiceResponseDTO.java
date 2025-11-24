package com.example.aura.Entity.Service.DTO;

import com.example.aura.Entity.Service.Domain.ServiceCategory;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ServiceResponseDTO {
    private Long id;
    private String name;
    private String description;
    private ServiceCategory category;
    private Integer totalTechnicians;
    private Integer totalReservations;
}
