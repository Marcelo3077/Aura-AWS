package com.example.aura.Entity.SupportTicket.DTO;

import com.example.aura.Entity.SupportTicket.Domain.TicketCategory;
import com.example.aura.Entity.SupportTicket.Domain.TicketPriority;
import com.example.aura.Entity.SupportTicket.Domain.TicketStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SupportTicketResponseDTO {
    private Long id;
    private Long reservationId;
    private String userName;
    private String subject;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private TicketStatus status;
    private TicketPriority priority;
    private TicketCategory category;
    private Long adminId;
    private String adminName;
    private Long paymentId;
}

