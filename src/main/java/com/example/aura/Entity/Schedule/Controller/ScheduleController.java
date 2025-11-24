package com.example.aura.Entity.Schedule.Controller;

import com.example.aura.Entity.Schedule.DTO.ScheduleRequestDTO;
import com.example.aura.Entity.Schedule.DTO.ScheduleResponseDTO;
import com.example.aura.Entity.Schedule.Domain.DayOfWeek;
import com.example.aura.Entity.Schedule.Domain.ScheduleStatus;
import com.example.aura.Entity.Schedule.Service.ScheduleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/schedules")
@RequiredArgsConstructor
public class ScheduleController {

    private final ScheduleService scheduleService;

    @PostMapping
    @PreAuthorize("hasAnyRole('TECHNICIAN', 'ADMIN', 'SUPERADMIN')")
    public ResponseEntity<ScheduleResponseDTO> createSchedule(@Valid @RequestBody ScheduleRequestDTO requestDTO) {
        ScheduleResponseDTO response = scheduleService.createSchedule(requestDTO);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ScheduleResponseDTO> getScheduleById(@PathVariable Long id) {
        ScheduleResponseDTO response = scheduleService.getScheduleById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<ScheduleResponseDTO>> getAllSchedules() {
        List<ScheduleResponseDTO> response = scheduleService.getAllSchedules();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/day/{dayOfWeek}")
    public ResponseEntity<List<ScheduleResponseDTO>> getSchedulesByDayOfWeek(@PathVariable DayOfWeek dayOfWeek) {
        List<ScheduleResponseDTO> response = scheduleService.getSchedulesByDayOfWeek(dayOfWeek);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/available")
    public ResponseEntity<List<ScheduleResponseDTO>> getAvailableSchedules() {
        List<ScheduleResponseDTO> response = scheduleService.getAvailableSchedules();
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('TECHNICIAN', 'ADMIN', 'SUPERADMIN')")
    public ResponseEntity<ScheduleResponseDTO> updateSchedule(
            @PathVariable Long id,
            @Valid @RequestBody ScheduleRequestDTO requestDTO) {
        ScheduleResponseDTO response = scheduleService.updateSchedule(id, requestDTO);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('TECHNICIAN', 'ADMIN', 'SUPERADMIN')")
    public ResponseEntity<ScheduleResponseDTO> updateScheduleStatus(
            @PathVariable Long id,
            @RequestParam ScheduleStatus status) {
        ScheduleResponseDTO response = scheduleService.updateScheduleStatus(id, status);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERADMIN')")
    public ResponseEntity<Void> deleteSchedule(@PathVariable Long id) {
        scheduleService.deleteSchedule(id);
        return ResponseEntity.noContent().build();
    }
}