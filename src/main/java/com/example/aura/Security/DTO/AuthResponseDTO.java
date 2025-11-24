package com.example.aura.Security.DTO;

import com.example.aura.Security.Domain.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthResponseDTO {
    private String token;
    private String type = "Bearer";
    private Long userId;
    private String email;
    private String firstName;
    private String lastName;
    private Role role;
}
