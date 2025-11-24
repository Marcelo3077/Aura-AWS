package com.example.aura.Entity.Reservation.Controller;

import com.example.aura.Entity.Reservation.DTO.ReservationRequestDTO;
import com.example.aura.Entity.Reservation.DTO.ReservationResponseDTO;
import com.example.aura.Entity.Reservation.Service.ReservationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reservations")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ReservationResponseDTO> createReservation(@Valid @RequestBody ReservationRequestDTO requestDTO) {
        ReservationResponseDTO response = reservationService.createReservation(requestDTO);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'TECHNICIAN', 'ADMIN', 'SUPERADMIN')")
    public ResponseEntity<ReservationResponseDTO> getReservationById(@PathVariable Long id) {
        ReservationResponseDTO response = reservationService.getReservationById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERADMIN')")
    public ResponseEntity<List<ReservationResponseDTO>> getAllReservations() {
        List<ReservationResponseDTO> response = reservationService.getAllReservations();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'SUPERADMIN')")
    public ResponseEntity<List<ReservationResponseDTO>> getReservationsByUserId(@PathVariable Long userId) {
        List<ReservationResponseDTO> response = reservationService.getReservationsByUserId(userId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/technician/{technicianId}")
    @PreAuthorize("hasAnyRole('TECHNICIAN', 'ADMIN', 'SUPERADMIN')")
    public ResponseEntity<List<ReservationResponseDTO>> getReservationsByTechnicianId(@PathVariable Long technicianId) {
        List<ReservationResponseDTO> response = reservationService.getReservationsByTechnicianId(technicianId);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/confirm")
    @PreAuthorize("hasRole('TECHNICIAN')")
    public ResponseEntity<ReservationResponseDTO> confirmReservation(@PathVariable Long id) {
        ReservationResponseDTO response = reservationService.confirmReservation(id);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/cancel")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'SUPERADMIN')")
    public ResponseEntity<ReservationResponseDTO> cancelReservation(@PathVariable Long id) {
        ReservationResponseDTO response = reservationService.cancelReservation(id);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/complete")
    @PreAuthorize("hasRole('TECHNICIAN')")
    public ResponseEntity<ReservationResponseDTO> completeReservation(@PathVariable Long id) {
        ReservationResponseDTO response = reservationService.completeReservation(id);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERADMIN')")
    public ResponseEntity<Void> deleteReservation(@PathVariable Long id) {
        reservationService.deleteReservation(id);
        return ResponseEntity.noContent().build();
    }
}