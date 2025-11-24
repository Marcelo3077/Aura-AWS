package com.example.aura.Entity.Technician.Controller;

import com.example.aura.Entity.Schedule.Domain.DayOfWeek;
import com.example.aura.Entity.Service.Domain.ServiceCategory;
import com.example.aura.Entity.Technician.DTO.TechnicianRequestDTO;
import com.example.aura.Entity.Technician.DTO.TechnicianResponseDTO;
import com.example.aura.Entity.Technician.DTO.TechnicianUpdateDTO;
import com.example.aura.Entity.Technician.Service.TechnicianService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/technicians")
@RequiredArgsConstructor
public class TechnicianController {

    private final TechnicianService technicianService;

    @PostMapping
    public ResponseEntity<TechnicianResponseDTO> createTechnician(@Valid @RequestBody TechnicianRequestDTO requestDTO) {
        TechnicianResponseDTO response = technicianService.createTechnician(requestDTO);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TechnicianResponseDTO> getTechnicianById(@PathVariable Long id) {
        TechnicianResponseDTO response = technicianService.getTechnicianById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<TechnicianResponseDTO> getTechnicianByEmail(@PathVariable String email) {
        TechnicianResponseDTO response = technicianService.getTechnicianByEmail(email);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<TechnicianResponseDTO>> getAllTechnicians() {
        List<TechnicianResponseDTO> response = technicianService.getAllTechnicians();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/specialty/{specialty}")
    public ResponseEntity<List<TechnicianResponseDTO>> getTechniciansBySpecialty(@PathVariable ServiceCategory specialty) {
        List<TechnicianResponseDTO> response = technicianService.getTechniciansBySpecialty(specialty);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/name/{firstName}")
    public ResponseEntity<List<TechnicianResponseDTO>> getTechniciansByFirstName(@PathVariable String firstName) {
        List<TechnicianResponseDTO> response = technicianService.getTechniciansByFirstName(firstName);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/available/{dayOfWeek}")
    public ResponseEntity<List<TechnicianResponseDTO>> getAvailableTechniciansByDay(@PathVariable DayOfWeek dayOfWeek) {
        List<TechnicianResponseDTO> response = technicianService.getAvailableTechniciansByDay(dayOfWeek);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/certified")
    public ResponseEntity<List<TechnicianResponseDTO>> getCertifiedTechnicians() {
        List<TechnicianResponseDTO> response = technicianService.getCertifiedTechnicians();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/count/specialty/{specialty}")
    public ResponseEntity<Long> countTechniciansBySpecialty(@PathVariable ServiceCategory specialty) {
        Long count = technicianService.countTechniciansBySpecialty(specialty);
        return ResponseEntity.ok(count);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('TECHNICIAN', 'ADMIN', 'SUPERADMIN')")
    public ResponseEntity<TechnicianResponseDTO> updateTechnician(
            @PathVariable Long id,
            @Valid @RequestBody TechnicianUpdateDTO updateDTO) {
        TechnicianResponseDTO response = technicianService.updateTechnician(id, updateDTO);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{technicianId}/schedules/{scheduleId}")
    @PreAuthorize("hasAnyRole('TECHNICIAN', 'ADMIN', 'SUPERADMIN')")
    public ResponseEntity<TechnicianResponseDTO> addScheduleToTechnician(
            @PathVariable Long technicianId,
            @PathVariable Long scheduleId) {
        TechnicianResponseDTO response = technicianService.addScheduleToTechnician(technicianId, scheduleId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{technicianId}/schedules/{scheduleId}")
    @PreAuthorize("hasAnyRole('TECHNICIAN', 'ADMIN', 'SUPERADMIN')")
    public ResponseEntity<TechnicianResponseDTO> removeScheduleFromTechnician(
            @PathVariable Long technicianId,
            @PathVariable Long scheduleId) {
        TechnicianResponseDTO response = technicianService.removeScheduleFromTechnician(technicianId, scheduleId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERADMIN')")
    public ResponseEntity<Void> deleteTechnician(@PathVariable Long id) {
        technicianService.deleteTechnician(id);
        return ResponseEntity.noContent().build();
    }
}
