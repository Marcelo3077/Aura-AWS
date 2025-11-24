package com.example.aura.Entity.SupportTicket.Controller;

import com.example.aura.Entity.SupportTicket.DTO.SupportTicketRequestDTO;
import com.example.aura.Entity.SupportTicket.DTO.SupportTicketResponseDTO;
import com.example.aura.Entity.SupportTicket.Domain.TicketStatus;
import com.example.aura.Entity.SupportTicket.Service.SupportTicketService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/support-tickets")
@RequiredArgsConstructor
public class SupportTicketController {

    private final SupportTicketService supportTicketService;

    @PostMapping
    @PreAuthorize("hasAnyRole('USER', 'TECHNICIAN')")
    public ResponseEntity<SupportTicketResponseDTO> createSupportTicket(@Valid @RequestBody SupportTicketRequestDTO requestDTO) {
        SupportTicketResponseDTO response = supportTicketService.createSupportTicket(requestDTO);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'TECHNICIAN', 'ADMIN', 'SUPERADMIN')")
    public ResponseEntity<SupportTicketResponseDTO> getSupportTicketById(@PathVariable Long id) {
        SupportTicketResponseDTO response = supportTicketService.getSupportTicketById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERADMIN')")
    public ResponseEntity<List<SupportTicketResponseDTO>> getAllSupportTickets() {
        List<SupportTicketResponseDTO> response = supportTicketService.getAllSupportTickets();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/status/{status}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERADMIN')")
    public ResponseEntity<List<SupportTicketResponseDTO>> getTicketsByStatus(@PathVariable TicketStatus status) {
        List<SupportTicketResponseDTO> response = supportTicketService.getTicketsByStatus(status);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/reservation/{reservationId}")
    @PreAuthorize("hasAnyRole('USER', 'TECHNICIAN', 'ADMIN', 'SUPERADMIN')")
    public ResponseEntity<List<SupportTicketResponseDTO>> getTicketsByReservationId(@PathVariable Long reservationId) {
        List<SupportTicketResponseDTO> response = supportTicketService.getTicketsByReservationId(reservationId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/admin/{adminId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERADMIN')")
    public ResponseEntity<List<SupportTicketResponseDTO>> getTicketsByAdminId(@PathVariable Long adminId) {
        List<SupportTicketResponseDTO> response = supportTicketService.getTicketsByAdminId(adminId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/unassigned")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERADMIN')")
    public ResponseEntity<List<SupportTicketResponseDTO>> getUnassignedTickets() {
        List<SupportTicketResponseDTO> response = supportTicketService.getUnassignedTickets();
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{ticketId}/assign/{adminId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERADMIN')")
    public ResponseEntity<SupportTicketResponseDTO> assignTicketToAdmin(
            @PathVariable Long ticketId,
            @PathVariable Long adminId) {
        SupportTicketResponseDTO response = supportTicketService.assignTicketToAdmin(ticketId, adminId);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/resolve")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERADMIN')")
    public ResponseEntity<SupportTicketResponseDTO> resolveTicket(@PathVariable Long id) {
        SupportTicketResponseDTO response = supportTicketService.resolveTicket(id);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/close")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERADMIN')")
    public ResponseEntity<SupportTicketResponseDTO> closeTicket(@PathVariable Long id) {
        SupportTicketResponseDTO response = supportTicketService.closeTicket(id);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERADMIN')")
    public ResponseEntity<Void> deleteSupportTicket(@PathVariable Long id) {
        supportTicketService.deleteSupportTicket(id);
        return ResponseEntity.noContent().build();
    }
}
