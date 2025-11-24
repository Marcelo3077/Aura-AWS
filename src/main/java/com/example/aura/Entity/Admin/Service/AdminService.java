package com.example.aura.Entity.Admin.Service;

import com.example.aura.Entity.Admin.DTO.AdminRequestDTO;
import com.example.aura.Entity.Admin.DTO.AdminResponseDTO;
import com.example.aura.Entity.Admin.Domain.AccessLevel;
import com.example.aura.Entity.Admin.Domain.Admin;
import com.example.aura.Entity.Admin.Repository.AdminRepository;
import com.example.aura.Entity.Certification.Domain.Certification;
import com.example.aura.Entity.Certification.Repository.CertificationRepository;
import com.example.aura.Event.Certification.CertificationValidatedEvent;
import com.example.aura.Exception.ConflictException;
import com.example.aura.Exception.ResourceNotFoundException;
import com.example.aura.Security.Domain.Role;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final AdminRepository adminRepository;
    private final CertificationRepository certificationRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public AdminResponseDTO createAdmin(AdminRequestDTO requestDTO) {
        if (adminRepository.findByEmail(requestDTO.getEmail()).isPresent()) {
            throw new ConflictException("Admin", "email", requestDTO.getEmail());
        }

        Admin admin = new Admin();
        admin.setFirstName(requestDTO.getFirstName());
        admin.setLastName(requestDTO.getLastName());
        admin.setEmail(requestDTO.getEmail());
        admin.setPasswordHash(passwordEncoder.encode(requestDTO.getPassword()));
        admin.setRegisterDate(LocalDate.now());
        admin.setAccessLevel(requestDTO.getAccessLevel());
        admin.setRole(requestDTO.getAccessLevel() == AccessLevel.SUPERADMIN ? Role.SUPERADMIN : Role.ADMIN);
        admin.setEnabled(true);

        Admin savedAdmin = adminRepository.save(admin);
        return mapToResponseDTO(savedAdmin);
    }

    @Transactional(readOnly = true)
    public AdminResponseDTO getAdminById(Long id) {
        Admin admin = adminRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Admin", "id", id));
        return mapToResponseDTO(admin);
    }

    @Transactional(readOnly = true)
    public AdminResponseDTO getAdminByEmail(String email) {
        Admin admin = adminRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Admin", "email", email));
        return mapToResponseDTO(admin);
    }

    @Transactional(readOnly = true)
    public List<AdminResponseDTO> getAllAdmins() {
        return adminRepository.findAll().stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<AdminResponseDTO> getAdminsByAccessLevel(AccessLevel accessLevel) {
        return adminRepository.findByAccessLevel(accessLevel).stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public AdminResponseDTO updateAdminAccessLevel(Long id, AccessLevel newAccessLevel) {
        Admin admin = adminRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Admin", "id", id));

        admin.setAccessLevel(newAccessLevel);
        Admin updatedAdmin = adminRepository.save(admin);
        return mapToResponseDTO(updatedAdmin);
    }

    @Transactional
    public void deleteAdmin(Long id) {
        Admin admin = adminRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Admin", "id", id));

        if (admin.getTickets() != null) {
            admin.getTickets().forEach(ticket -> ticket.setAdmin(null));
        }

        adminRepository.delete(admin);
    }

    @Transactional
    public AdminResponseDTO verifyCertification(Long adminId, Long certificationId) {
        Admin admin = adminRepository.findById(adminId)
                .orElseThrow(() -> new ResourceNotFoundException("Admin", "id", adminId));

        Certification certification = certificationRepository.findById(certificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Certification", "id", certificationId));

        if (!admin.getVerifiedCertifications().contains(certification)) {
            admin.getVerifiedCertifications().add(certification);
            certification.getAdmins().add(admin);
            certification.setValidated(true);
            certificationRepository.save(certification);
            adminRepository.save(admin);
            eventPublisher.publishEvent(new CertificationValidatedEvent(
                    this,
                    certification.getId(),
                    certification.getTechnician().getId(),
                    certification.getTechnician().getEmail(),
                    certification.getTitle(),
                    admin.getFirstName() + " " + admin.getLastName()
            ));
        }

        return mapToResponseDTO(admin);
    }

    private AdminResponseDTO mapToResponseDTO(Admin admin) {
        AdminResponseDTO dto = modelMapper.map(admin, AdminResponseDTO.class);
        dto.setTotalTicketsManaged(admin.getTickets() != null ? admin.getTickets().size() : 0);
        dto.setTotalCertificationsVerified(admin.getVerifiedCertifications() != null ?
                admin.getVerifiedCertifications().size() : 0);
        return dto;
    }

    private String encryptPassword(String password) {
        return password;
    }
}
