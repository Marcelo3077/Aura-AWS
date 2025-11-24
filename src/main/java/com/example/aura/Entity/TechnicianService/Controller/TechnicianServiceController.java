package com.example.aura.Entity.TechnicianService.Controller;

import com.example.aura.Entity.TechnicianService.DTO.TechnicianServiceRequestDTO;
import com.example.aura.Entity.TechnicianService.DTO.TechnicianServiceResponseDTO;
import com.example.aura.Entity.TechnicianService.Service.TechnicianServiceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/technician-services")
@RequiredArgsConstructor
public class TechnicianServiceController {

    private final TechnicianServiceService technicianServiceService;

    @PostMapping
    @PreAuthorize("hasAnyRole('TECHNICIAN', 'ADMIN', 'SUPERADMIN')")
    public ResponseEntity<TechnicianServiceResponseDTO> createTechnicianService(
            @Valid @RequestBody TechnicianServiceRequestDTO requestDTO) {
        TechnicianServiceResponseDTO response = technicianServiceService.createTechnicianService(requestDTO);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{technicianId}/{serviceId}")
    public ResponseEntity<TechnicianServiceResponseDTO> getTechnicianServiceById(
            @PathVariable Long technicianId,
            @PathVariable Long serviceId) {
        TechnicianServiceResponseDTO response = technicianServiceService.getTechnicianServiceById(technicianId, serviceId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/technician/{technicianId}")
    public ResponseEntity<List<TechnicianServiceResponseDTO>> getServicesByTechnicianId(@PathVariable Long technicianId) {
        List<TechnicianServiceResponseDTO> response = technicianServiceService.getServicesByTechnicianId(technicianId);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<TechnicianServiceResponseDTO>> getAllTechnicianServices() {
        List<TechnicianServiceResponseDTO> response = technicianServiceService.getAllTechnicianServices();
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{technicianId}/{serviceId}/base-rate")
    @PreAuthorize("hasAnyRole('TECHNICIAN', 'ADMIN', 'SUPERADMIN')")
    public ResponseEntity<TechnicianServiceResponseDTO> updateBaseRate(
            @PathVariable Long technicianId,
            @PathVariable Long serviceId,
            @RequestParam Double baseRate) {
        TechnicianServiceResponseDTO response = technicianServiceService.updateBaseRate(technicianId, serviceId, baseRate);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{technicianId}/{serviceId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERADMIN')")
    public ResponseEntity<Void> deleteTechnicianService(
            @PathVariable Long technicianId,
            @PathVariable Long serviceId) {
        technicianServiceService.deleteTechnicianService(technicianId, serviceId);
        return ResponseEntity.noContent().build();
    }
}
