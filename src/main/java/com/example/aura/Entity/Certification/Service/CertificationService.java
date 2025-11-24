package com.example.aura.Entity.Certification.Service;

import com.example.aura.Entity.Admin.Domain.Admin;
import com.example.aura.Entity.Admin.Repository.AdminRepository;
import com.example.aura.Entity.Certification.DTO.CertificationRequestDTO;
import com.example.aura.Entity.Certification.DTO.CertificationResponseDTO;
import com.example.aura.Entity.Certification.Domain.Certification;
import com.example.aura.Entity.Certification.Repository.CertificationRepository;
import com.example.aura.Entity.Technician.Domain.Technician;
import com.example.aura.Entity.Technician.Repository.TechnicianRepository;
import com.example.aura.Exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CertificationService {

    private final CertificationRepository certificationRepository;
    private final TechnicianRepository technicianRepository;
    private final AdminRepository adminRepository;
    private final ModelMapper modelMapper;

    @Transactional
    public CertificationResponseDTO createCertification(CertificationRequestDTO requestDTO) {
        Technician technician = technicianRepository.findById(requestDTO.getTechnicianId())
                .orElseThrow(() -> new ResourceNotFoundException("Technician", "id", requestDTO.getTechnicianId()));

        Certification certification = new Certification();
        certification.setTechnician(technician);
        certification.setTitle(requestDTO.getTitle());
        certification.setDescription(requestDTO.getDescription());
        certification.setIssueDate(requestDTO.getIssueDate());
        certification.setValidated(false);

        Certification savedCertification = certificationRepository.save(certification);
        return mapToResponseDTO(savedCertification);
    }

    @Transactional(readOnly = true)
    public CertificationResponseDTO getCertificationById(Long id) {
        Certification certification = certificationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Certification", "id", id));
        return mapToResponseDTO(certification);
    }

    @Transactional(readOnly = true)
    public List<CertificationResponseDTO> getAllCertifications() {
        return certificationRepository.findAll().stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<CertificationResponseDTO> getCertificationsByTechnicianId(Long technicianId) {
        return certificationRepository.findAll().stream()
                .filter(c -> c.getTechnician().getId().equals(technicianId))
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<CertificationResponseDTO> getValidatedCertifications() {
        return certificationRepository.findAll().stream()
                .filter(Certification::getValidated)
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<CertificationResponseDTO> getPendingCertifications() {
        return certificationRepository.findAll().stream()
                .filter(c -> !c.getValidated())
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public CertificationResponseDTO updateCertification(Long id, CertificationRequestDTO requestDTO) {
        Certification certification = certificationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Certification", "id", id));

        certification.setTitle(requestDTO.getTitle());
        certification.setDescription(requestDTO.getDescription());
        certification.setIssueDate(requestDTO.getIssueDate());

        Certification updatedCertification = certificationRepository.save(certification);
        return mapToResponseDTO(updatedCertification);
    }

    @Transactional
    public CertificationResponseDTO validateCertification(Long certificationId, Long adminId) {
        Certification certification = certificationRepository.findById(certificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Certification", "id", certificationId));

        Admin admin = adminRepository.findById(adminId)
                .orElseThrow(() -> new ResourceNotFoundException("Admin", "id", adminId));

        if (!certification.getAdmins().contains(admin)) {
            certification.getAdmins().add(admin);
            admin.getVerifiedCertifications().add(certification);
        }

        certification.setValidated(true);
        Certification validatedCertification = certificationRepository.save(certification);
        return mapToResponseDTO(validatedCertification);
    }

    @Transactional
    public CertificationResponseDTO rejectCertification(Long id) {
        Certification certification = certificationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Certification", "id", id));

        certification.setValidated(false);
        certification.getAdmins().clear();

        Certification rejectedCertification = certificationRepository.save(certification);
        return mapToResponseDTO(rejectedCertification);
    }

    @Transactional
    public void deleteCertification(Long id) {
        Certification certification = certificationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Certification", "id", id));
        certificationRepository.delete(certification);
    }

    private CertificationResponseDTO mapToResponseDTO(Certification certification) {
        CertificationResponseDTO dto = new CertificationResponseDTO();

        dto.setId(certification.getId());
        dto.setTechnicianId(certification.getTechnician().getId());
        dto.setTechnicianName(certification.getTechnician().getFirstName() + " " +
                certification.getTechnician().getLastName());
        dto.setTitle(certification.getTitle());
        dto.setDescription(certification.getDescription());
        dto.setIssueDate(certification.getIssueDate());
        dto.setValidated(certification.getValidated());

        List<String> imageUrls = certification.getImages() != null ?
                certification.getImages().stream()
                        .map(img -> img.getFile())
                        .collect(Collectors.toList()) : List.of();
        dto.setImageUrls(imageUrls);

        List<String> verifiedBy = certification.getAdmins() != null ?
                certification.getAdmins().stream()
                        .map(admin -> admin.getFirstName() + " " + admin.getLastName())
                        .collect(Collectors.toList()) : List.of();
        dto.setVerifiedByAdmins(verifiedBy);

        return dto;
    }
}
