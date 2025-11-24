package com.example.aura.Entity.Technician.DTO;

import com.example.aura.Entity.Service.Domain.ServiceCategory;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TechnicianUpdateDTO {

    @Size(max = 50, message = "First name must not exceed 50 characters")
    private String firstName;

    @Size(max = 50, message = "Last name must not exceed 50 characters")
    private String lastName;

    @Pattern(regexp = "^\\+?[0-9]{9,20}$", message = "Phone must be valid")
    private String phone;

    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;

    private List<ServiceCategory> specialties;
}
