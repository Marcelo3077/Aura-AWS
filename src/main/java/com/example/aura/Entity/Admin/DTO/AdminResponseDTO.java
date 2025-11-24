package com.example.aura.Entity.Admin.DTO;

import com.example.aura.Entity.Admin.Domain.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminResponseDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private LocalDate registerDate;
    private AccessLevel accessLevel;
    private Integer totalTicketsManaged;
    private Integer totalCertificationsVerified;
}
