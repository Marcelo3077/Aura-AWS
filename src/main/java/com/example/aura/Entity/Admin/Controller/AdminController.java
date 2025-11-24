package com.example.aura.Entity.Admin.Controller;

import com.example.aura.Entity.Admin.DTO.AdminRequestDTO;
import com.example.aura.Entity.Admin.DTO.AdminResponseDTO;
import com.example.aura.Entity.Admin.Domain.AccessLevel;
import com.example.aura.Entity.Admin.Service.AdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admins")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN', 'SUPERADMIN')")
public class AdminController {

    private final AdminService adminService;

    @PostMapping
    @PreAuthorize("hasRole('SUPERADMIN')")
    public ResponseEntity<AdminResponseDTO> createAdmin(@Valid @RequestBody AdminRequestDTO requestDTO) {
        AdminResponseDTO response = adminService.createAdmin(requestDTO);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AdminResponseDTO> getAdminById(@PathVariable Long id) {
        AdminResponseDTO response = adminService.getAdminById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<AdminResponseDTO> getAdminByEmail(@PathVariable String email) {
        AdminResponseDTO response = adminService.getAdminByEmail(email);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<AdminResponseDTO>> getAllAdmins() {
        List<AdminResponseDTO> response = adminService.getAllAdmins();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/access-level/{accessLevel}")
    public ResponseEntity<List<AdminResponseDTO>> getAdminsByAccessLevel(@PathVariable AccessLevel accessLevel) {
        List<AdminResponseDTO> response = adminService.getAdminsByAccessLevel(accessLevel);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/access-level")
    @PreAuthorize("hasRole('SUPERADMIN')")
    public ResponseEntity<AdminResponseDTO> updateAdminAccessLevel(
            @PathVariable Long id,
            @RequestParam AccessLevel accessLevel) {
        AdminResponseDTO response = adminService.updateAdminAccessLevel(id, accessLevel);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{adminId}/verify-certification/{certificationId}")
    public ResponseEntity<AdminResponseDTO> verifyCertification(
            @PathVariable Long adminId,
            @PathVariable Long certificationId) {
        AdminResponseDTO response = adminService.verifyCertification(adminId, certificationId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('SUPERADMIN')")
    public ResponseEntity<Void> deleteAdmin(@PathVariable Long id) {
        adminService.deleteAdmin(id);
        return ResponseEntity.noContent().build();
    }
}