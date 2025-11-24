package com.example.aura.Entity.Payment.DTO;

import com.example.aura.Entity.Payment.Domain.PaymentMethod;
import com.example.aura.Entity.Payment.Domain.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponseDTO {
    private Long id;
    private Long reservationId;
    private Double amount;
    private LocalDate paymentDate;
    private PaymentMethod paymentMethod;
    private PaymentStatus paymentStatus;
}
