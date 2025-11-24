package com.example.aura.Security.Service;

import com.example.aura.Entity.Admin.Domain.Admin;
import com.example.aura.Entity.Superuser.Domain.Superuser;
import com.example.aura.Entity.Technician.Domain.Technician;
import com.example.aura.Entity.User.Domain.User;
import com.example.aura.Exception.ConflictException;
import com.example.aura.Security.DTO.AuthResponseDTO;
import com.example.aura.Security.DTO.LoginRequestDTO;
import com.example.aura.Security.DTO.RegisterRequestDTO;
import com.example.aura.Security.Domain.Role;
import com.example.aura.Service.AuditService;
import com.example.aura.Service.NotificationService;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.aura.Event.User.UserRegisteredEvent;
import org.springframework.context.ApplicationEventPublisher;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final EntityManager entityManager;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final NotificationService notificationService;
    private final AuditService auditService;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public AuthResponseDTO register(RegisterRequestDTO request) {
        String checkEmailQuery = "SELECT COUNT(s) FROM Superuser s WHERE s.email = :email";
        Long count = entityManager.createQuery(checkEmailQuery, Long.class)
                .setParameter("email", request.getEmail())
                .getSingleResult();

        if (count > 0) {
            throw new ConflictException("User", "email", request.getEmail());
        }

        Superuser superuser;

        switch (request.getRole()) {
            case USER:
                User user = new User();
                user.setRole(Role.USER);
                superuser = user;
                break;

            case TECHNICIAN:
                Technician technician = new Technician();
                technician.setRole(Role.TECHNICIAN);
                technician.setDescription(request.getDescription());
                technician.setSpecialties(request.getSpecialties());
                superuser = technician;
                break;

            case ADMIN:
            case SUPERADMIN:
                Admin admin = new Admin();
                admin.setRole(request.getRole());
                superuser = admin;
                break;

            default:
                throw new IllegalArgumentException("Invalid role: " + request.getRole());
        }

        superuser.setFirstName(request.getFirstName());
        superuser.setLastName(request.getLastName());
        superuser.setEmail(request.getEmail());
        superuser.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        superuser.setPhone(request.getPhone());
        superuser.setRegisterDate(LocalDate.now());
        superuser.setEnabled(true);

        entityManager.persist(superuser);
        entityManager.flush();

        String jwtToken = jwtService.generateToken(superuser);

        eventPublisher.publishEvent(new UserRegisteredEvent(
                this,
                superuser.getId(),
                superuser.getEmail(),
                superuser.getFirstName(),
                superuser.getRole()
        ));

        return AuthResponseDTO.builder()
                .token(jwtToken)
                .type("Bearer")
                .userId(superuser.getId())
                .email(superuser.getEmail())
                .firstName(superuser.getFirstName())
                .lastName(superuser.getLastName())
                .role(superuser.getRole())
                .build();
    }


    public AuthResponseDTO login(LoginRequestDTO request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        String query = "SELECT s FROM Superuser s WHERE s.email = :email";
        Superuser superuser = entityManager.createQuery(query, Superuser.class)
                .setParameter("email", request.getEmail())
                .getSingleResult();

        String jwtToken = jwtService.generateToken(superuser);

        return AuthResponseDTO.builder()
                .token(jwtToken)
                .type("Bearer")
                .userId(superuser.getId())
                .email(superuser.getEmail())
                .firstName(superuser.getFirstName())
                .lastName(superuser.getLastName())
                .role(superuser.getRole())
                .build();
    }
}
