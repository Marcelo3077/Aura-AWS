package com.example.aura.Entity.SupportTicket.DTO;

import com.example.aura.Entity.SupportTicket.Domain.TicketCategory;
import com.example.aura.Entity.SupportTicket.Domain.TicketPriority;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SupportTicketRequestDTO {

    @NotNull(message = "Reservation ID is required")
    private Long reservationId;

    @NotBlank(message = "Subject is required")
    @Size(max = 100, message = "Subject must not exceed 100 characters")
    private String subject;

    @Size(max = 300, message = "Description must not exceed 300 characters")
    private String description;

    @NotNull(message = "Priority is required")
    private TicketPriority priority;

    @NotNull(message = "Category is required")
    private TicketCategory category;

    private Long paymentId;
}
