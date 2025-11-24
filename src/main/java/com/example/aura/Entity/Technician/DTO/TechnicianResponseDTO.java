package com.example.aura.Entity.Technician.DTO;

import com.example.aura.Entity.Service.Domain.ServiceCategory;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TechnicianResponseDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private LocalDate registerDate;
    private String description;
    private List<ServiceCategory> specialties;
    private Integer totalServices;
    private Integer totalCertifications;
    private Integer validatedCertifications;
    private Double averageRating;
}
