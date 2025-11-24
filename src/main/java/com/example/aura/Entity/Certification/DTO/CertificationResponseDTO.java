package com.example.aura.Entity.Certification.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CertificationResponseDTO {
    private Long id;
    private Long technicianId;
    private String technicianName;
    private String title;
    private String description;
    private LocalDate issueDate;
    private Boolean validated;
    private List<String> imageUrls;
    private List<String> verifiedByAdmins;
}
