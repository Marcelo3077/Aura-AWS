package com.example.aura.Entity.Payment.Controller;

import com.example.aura.Entity.Payment.DTO.PaymentRequestDTO;
import com.example.aura.Entity.Payment.DTO.PaymentResponseDTO;
import com.example.aura.Entity.Payment.Domain.PaymentStatus;
import com.example.aura.Entity.Payment.Service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'SUPERADMIN')")
    public ResponseEntity<PaymentResponseDTO> createPayment(@Valid @RequestBody PaymentRequestDTO requestDTO) {
        PaymentResponseDTO response = paymentService.createPayment(requestDTO);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'TECHNICIAN', 'ADMIN', 'SUPERADMIN')")
    public ResponseEntity<PaymentResponseDTO> getPaymentById(@PathVariable Long id) {
        PaymentResponseDTO response = paymentService.getPaymentById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERADMIN')")
    public ResponseEntity<List<PaymentResponseDTO>> getAllPayments() {
        List<PaymentResponseDTO> response = paymentService.getAllPayments();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/reservation/{reservationId}")
    @PreAuthorize("hasAnyRole('USER', 'TECHNICIAN', 'ADMIN', 'SUPERADMIN')")
    public ResponseEntity<List<PaymentResponseDTO>> getPaymentsByReservationId(@PathVariable Long reservationId) {
        List<PaymentResponseDTO> response = paymentService.getPaymentsByReservationId(reservationId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/status/{status}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERADMIN')")
    public ResponseEntity<List<PaymentResponseDTO>> getPaymentsByStatus(@PathVariable PaymentStatus status) {
        List<PaymentResponseDTO> response = paymentService.getPaymentsByStatus(status);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/process")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERADMIN')")
    public ResponseEntity<PaymentResponseDTO> processPayment(@PathVariable Long id) {
        PaymentResponseDTO response = paymentService.processPayment(id);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/fail")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERADMIN')")
    public ResponseEntity<PaymentResponseDTO> failPayment(@PathVariable Long id) {
        PaymentResponseDTO response = paymentService.failPayment(id);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/refund")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERADMIN')")
    public ResponseEntity<PaymentResponseDTO> refundPayment(@PathVariable Long id) {
        PaymentResponseDTO response = paymentService.refundPayment(id);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/cancel")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'SUPERADMIN')")
    public ResponseEntity<PaymentResponseDTO> cancelPayment(@PathVariable Long id) {
        PaymentResponseDTO response = paymentService.cancelPayment(id);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERADMIN')")
    public ResponseEntity<Void> deletePayment(@PathVariable Long id) {
        paymentService.deletePayment(id);
        return ResponseEntity.noContent().build();
    }
}