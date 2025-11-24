package com.example.aura.Entity.Payment.DTO;

import com.example.aura.Entity.Payment.Domain.PaymentMethod;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentRequestDTO {

    @NotNull(message = "Reservation ID is required")
    private Long reservationId;

    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be positive")
    private Double amount;

    @NotNull(message = "Payment method is required")
    private PaymentMethod paymentMethod;
}
