package com.example.aura.Entity.SupportTicket.Service;

import com.example.aura.Entity.Admin.Domain.Admin;
import com.example.aura.Entity.Admin.Repository.AdminRepository;
import com.example.aura.Entity.Payment.Domain.Payment;
import com.example.aura.Entity.Payment.Repository.PaymentRepository;
import com.example.aura.Entity.Reservation.Domain.Reservation;
import com.example.aura.Entity.Reservation.Repository.ReservationRepository;
import com.example.aura.Entity.SupportTicket.DTO.SupportTicketRequestDTO;
import com.example.aura.Entity.SupportTicket.DTO.SupportTicketResponseDTO;
import com.example.aura.Entity.SupportTicket.Domain.SupportTicket;
import com.example.aura.Entity.SupportTicket.Domain.TicketStatus;
import com.example.aura.Entity.SupportTicket.Repository.SupportTicketRepository;
import com.example.aura.Event.SupportTicket.TicketCreatedEvent;
import com.example.aura.Exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SupportTicketService {

    private final SupportTicketRepository supportTicketRepository;
    private final ReservationRepository reservationRepository;
    private final AdminRepository adminRepository;
    private final PaymentRepository paymentRepository;
    private final ModelMapper modelMapper;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public SupportTicketResponseDTO createSupportTicket(SupportTicketRequestDTO requestDTO) {
        Reservation reservation = reservationRepository.findById(requestDTO.getReservationId())
                .orElseThrow(() -> new ResourceNotFoundException("Reservation", "id", requestDTO.getReservationId()));

        SupportTicket ticket = new SupportTicket();
        ticket.setReservation(reservation);
        ticket.setSubject(requestDTO.getSubject());
        ticket.setDescription(requestDTO.getDescription());
        ticket.setCreatedAt(LocalDateTime.now());
        ticket.setUpdatedAt(LocalDateTime.now());
        ticket.setStatus(TicketStatus.OPEN);
        ticket.setPriority(requestDTO.getPriority());
        ticket.setCategory(requestDTO.getCategory());

        if (requestDTO.getPaymentId() != null) {
            Payment payment = paymentRepository.findById(requestDTO.getPaymentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Payment", "id", requestDTO.getPaymentId()));
            ticket.setPayment(payment);
        }

        SupportTicket savedTicket = supportTicketRepository.save(ticket);
        eventPublisher.publishEvent(new TicketCreatedEvent(
                this,
                savedTicket.getId(),
                savedTicket.getSubject(),
                savedTicket.getPriority(),
                savedTicket.getReservation().getUser().getEmail()
        ));
        return mapToResponseDTO(savedTicket);
    }

    @Transactional(readOnly = true)
    public SupportTicketResponseDTO getSupportTicketById(Long id) {
        SupportTicket ticket = supportTicketRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("SupportTicket", "id", id));
        return mapToResponseDTO(ticket);
    }

    @Transactional(readOnly = true)
    public List<SupportTicketResponseDTO> getAllSupportTickets() {
        return supportTicketRepository.findAll().stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<SupportTicketResponseDTO> getTicketsByStatus(TicketStatus status) {
        return supportTicketRepository.findAll().stream()
                .filter(t -> t.getStatus() == status)
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<SupportTicketResponseDTO> getTicketsByReservationId(Long reservationId) {
        return supportTicketRepository.findAll().stream()
                .filter(t -> t.getReservation().getId().equals(reservationId))
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<SupportTicketResponseDTO> getTicketsByAdminId(Long adminId) {
        return supportTicketRepository.findAll().stream()
                .filter(t -> t.getAdmin() != null && t.getAdmin().getId().equals(adminId))
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<SupportTicketResponseDTO> getUnassignedTickets() {
        return supportTicketRepository.findAll().stream()
                .filter(t -> t.getAdmin() == null)
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public SupportTicketResponseDTO assignTicketToAdmin(Long ticketId, Long adminId) {
        SupportTicket ticket = supportTicketRepository.findById(ticketId)
                .orElseThrow(() -> new ResourceNotFoundException("SupportTicket", "id", ticketId));

        Admin admin = adminRepository.findById(adminId)
                .orElseThrow(() -> new ResourceNotFoundException("Admin", "id", adminId));

        ticket.setAdmin(admin);
        ticket.setStatus(TicketStatus.IN_PROGRESS);
        ticket.setUpdatedAt(LocalDateTime.now());

        SupportTicket updatedTicket = supportTicketRepository.save(ticket);
        return mapToResponseDTO(updatedTicket);
    }

    @Transactional
    public SupportTicketResponseDTO resolveTicket(Long id) {
        SupportTicket ticket = supportTicketRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("SupportTicket", "id", id));

        ticket.setStatus(TicketStatus.RESOLVED);
        ticket.setUpdatedAt(LocalDateTime.now());

        SupportTicket resolvedTicket = supportTicketRepository.save(ticket);
        return mapToResponseDTO(resolvedTicket);
    }

    @Transactional
    public SupportTicketResponseDTO closeTicket(Long id) {
        SupportTicket ticket = supportTicketRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("SupportTicket", "id", id));

        ticket.setStatus(TicketStatus.CLOSED);
        ticket.setUpdatedAt(LocalDateTime.now());

        SupportTicket closedTicket = supportTicketRepository.save(ticket);
        return mapToResponseDTO(closedTicket);
    }

    @Transactional
    public void deleteSupportTicket(Long id) {
        SupportTicket ticket = supportTicketRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("SupportTicket", "id", id));
        supportTicketRepository.delete(ticket);
    }

    private SupportTicketResponseDTO mapToResponseDTO(SupportTicket ticket) {
        SupportTicketResponseDTO dto = new SupportTicketResponseDTO();

        dto.setId(ticket.getId());
        dto.setReservationId(ticket.getReservation().getId());
        dto.setUserName(ticket.getReservation().getUser().getFirstName() + " " +
                ticket.getReservation().getUser().getLastName());
        dto.setSubject(ticket.getSubject());
        dto.setDescription(ticket.getDescription());
        dto.setCreatedAt(ticket.getCreatedAt());
        dto.setUpdatedAt(ticket.getUpdatedAt());
        dto.setStatus(ticket.getStatus());
        dto.setPriority(ticket.getPriority());
        dto.setCategory(ticket.getCategory());

        if (ticket.getAdmin() != null) {
            dto.setAdminId(ticket.getAdmin().getId());
            dto.setAdminName(ticket.getAdmin().getFirstName() + " " + ticket.getAdmin().getLastName());
        }

        if (ticket.getPayment() != null) {
            dto.setPaymentId(ticket.getPayment().getId());
        }

        return dto;
    }
}
