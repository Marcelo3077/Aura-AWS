package com.example.aura.Entity.Certification.Controller;

import com.example.aura.Entity.Certification.DTO.CertificationRequestDTO;
import com.example.aura.Entity.Certification.DTO.CertificationResponseDTO;
import com.example.aura.Entity.Certification.Service.CertificationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/certifications")
@RequiredArgsConstructor
public class CertificationController {

    private final CertificationService certificationService;

    @PostMapping
    @PreAuthorize("hasAnyRole('TECHNICIAN', 'ADMIN', 'SUPERADMIN')")
    public ResponseEntity<CertificationResponseDTO> createCertification(@Valid @RequestBody CertificationRequestDTO requestDTO) {
        CertificationResponseDTO response = certificationService.createCertification(requestDTO);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('TECHNICIAN', 'ADMIN', 'SUPERADMIN')")
    public ResponseEntity<CertificationResponseDTO> getCertificationById(@PathVariable Long id) {
        CertificationResponseDTO response = certificationService.getCertificationById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERADMIN')")
    public ResponseEntity<List<CertificationResponseDTO>> getAllCertifications() {
        List<CertificationResponseDTO> response = certificationService.getAllCertifications();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/technician/{technicianId}")
    public ResponseEntity<List<CertificationResponseDTO>> getCertificationsByTechnicianId(@PathVariable Long technicianId) {
        List<CertificationResponseDTO> response = certificationService.getCertificationsByTechnicianId(technicianId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/validated")
    public ResponseEntity<List<CertificationResponseDTO>> getValidatedCertifications() {
        List<CertificationResponseDTO> response = certificationService.getValidatedCertifications();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/pending")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERADMIN')")
    public ResponseEntity<List<CertificationResponseDTO>> getPendingCertifications() {
        List<CertificationResponseDTO> response = certificationService.getPendingCertifications();
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('TECHNICIAN', 'ADMIN', 'SUPERADMIN')")
    public ResponseEntity<CertificationResponseDTO> updateCertification(
            @PathVariable Long id,
            @Valid @RequestBody CertificationRequestDTO requestDTO) {
        CertificationResponseDTO response = certificationService.updateCertification(id, requestDTO);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{certificationId}/validate")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERADMIN')")
    public ResponseEntity<CertificationResponseDTO> validateCertification(
            @PathVariable Long certificationId,
            @RequestParam Long adminId) {
        CertificationResponseDTO response = certificationService.validateCertification(certificationId, adminId);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/reject")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERADMIN')")
    public ResponseEntity<CertificationResponseDTO> rejectCertification(@PathVariable Long id) {
        CertificationResponseDTO response = certificationService.rejectCertification(id);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERADMIN')")
    public ResponseEntity<Void> deleteCertification(@PathVariable Long id) {
        certificationService.deleteCertification(id);
        return ResponseEntity.noContent().build();
    }
}