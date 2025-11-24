package com.example.aura.Entity.Reservation.Service;

import com.example.aura.Entity.Reservation.DTO.ReservationRequestDTO;
import com.example.aura.Entity.Reservation.DTO.ReservationResponseDTO;
import com.example.aura.Entity.Reservation.DTO.ReservationUpdateDTO;
import com.example.aura.Entity.Reservation.Domain.Reservation;
import com.example.aura.Entity.Reservation.Domain.ReservationStatus;
import com.example.aura.Entity.Reservation.Repository.ReservationRepository;
import com.example.aura.Entity.TechnicianService.Domain.TechnicianService;
import com.example.aura.Entity.TechnicianService.Domain.TechnicianServiceId;
import com.example.aura.Entity.TechnicianService.Repository.TechnicianServiceRepository;
import com.example.aura.Entity.User.Domain.User;
import com.example.aura.Entity.User.Repository.UserRepository;
import com.example.aura.Exception.ResourceNotFoundException;
import com.example.aura.Service.AuditService;
import com.example.aura.Service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.aura.Event.Reservation.ReservationCreatedEvent;
import com.example.aura.Event.Reservation.ReservationConfirmedEvent;
import com.example.aura.Event.Reservation.ReservationCompletedEvent;
import org.springframework.context.ApplicationEventPublisher;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final UserRepository userRepository;
    private final TechnicianServiceRepository technicianServiceRepository;
    private final NotificationService notificationService;
    private final AuditService auditService;
    private final ModelMapper modelMapper;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public ReservationResponseDTO createReservation(ReservationRequestDTO requestDTO) {
        User user = userRepository.findById(requestDTO.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", requestDTO.getUserId()));

        TechnicianServiceId tsId = new TechnicianServiceId(requestDTO.getTechnicianId(), requestDTO.getServiceId());
        TechnicianService technicianService = technicianServiceRepository.findById(tsId)
                .orElseThrow(() -> new ResourceNotFoundException("TechnicianService", "id",
                        requestDTO.getTechnicianId() + "-" + requestDTO.getServiceId()));

        Reservation reservation = new Reservation();
        reservation.setUser(user);
        reservation.setTechnicianService(technicianService);
        reservation.setReservationDate(LocalDate.now());
        reservation.setServiceDate(requestDTO.getServiceDate());
        reservation.setStartTime(requestDTO.getStartTime());
        reservation.setAddress(requestDTO.getAddress());
        reservation.setStatus(ReservationStatus.PENDING);
        reservation.setCreatedAt(LocalDateTime.now());
        reservation.setUpdatedAt(LocalDateTime.now());

        Reservation savedReservation = reservationRepository.save(reservation);
        eventPublisher.publishEvent(new ReservationCreatedEvent(
                this,
                savedReservation.getId(),
                user.getId(),
                technicianService.getTechnician().getId(),
                user.getEmail(),
                technicianService.getTechnician().getEmail(),
                technicianService.getService().getName(),
                savedReservation.getServiceDate().toString()
        ));

        return mapToResponseDTO(savedReservation);
    }

    @Transactional(readOnly = true)
    public ReservationResponseDTO getReservationById(Long id) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation", "id", id));
        return mapToResponseDTO(reservation);
    }

    @Transactional(readOnly = true)
    public List<ReservationResponseDTO> getAllReservations() {
        return reservationRepository.findAll().stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ReservationResponseDTO> getReservationsByUserId(Long userId) {
        return reservationRepository.findAll().stream()
                .filter(r -> r.getUser().getId().equals(userId))
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ReservationResponseDTO> getReservationsByTechnicianId(Long technicianId) {
        return reservationRepository.findAll().stream()
                .filter(r -> r.getTechnicianService().getTechnician().getId().equals(technicianId))
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ReservationResponseDTO> getReservationsByStatus(ReservationStatus status) {
        return reservationRepository.findAll().stream()
                .filter(r -> r.getStatus() == status)
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public ReservationResponseDTO updateReservation(Long id, ReservationUpdateDTO updateDTO) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation", "id", id));

        if (updateDTO.getServiceDate() != null) {
            reservation.setServiceDate(updateDTO.getServiceDate());
        }
        if (updateDTO.getStartTime() != null) {
            reservation.setStartTime(updateDTO.getStartTime());
        }
        if (updateDTO.getEndTime() != null) {
            reservation.setEndTime(updateDTO.getEndTime());
        }
        if (updateDTO.getAddress() != null) {
            reservation.setAddress(updateDTO.getAddress());
        }
        if (updateDTO.getStatus() != null) {
            reservation.setStatus(updateDTO.getStatus());
        }

        reservation.setUpdatedAt(LocalDateTime.now());
        Reservation updatedReservation = reservationRepository.save(reservation);
        return mapToResponseDTO(updatedReservation);
    }

    @Transactional
    public ReservationResponseDTO confirmReservation(Long id) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation", "id", id));

        reservation.setStatus(ReservationStatus.CONFIRMED);
        reservation.setUpdatedAt(LocalDateTime.now());
        Reservation updatedReservation = reservationRepository.save(reservation);

        eventPublisher.publishEvent(new ReservationConfirmedEvent(
                this,
                updatedReservation.getId(),
                updatedReservation.getUser().getEmail(),
                updatedReservation.getUser().getPhone(),
                updatedReservation.getTechnicianService().getTechnician().getFirstName() + " " +
                        updatedReservation.getTechnicianService().getTechnician().getLastName(),
                updatedReservation.getServiceDate().toString()
        ));

        return mapToResponseDTO(updatedReservation);
    }

    @Transactional
    public ReservationResponseDTO cancelReservation(Long id) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation", "id", id));

        reservation.setStatus(ReservationStatus.CANCELLED);
        reservation.setUpdatedAt(LocalDateTime.now());
        Reservation updatedReservation = reservationRepository.save(reservation);
        return mapToResponseDTO(updatedReservation);
    }

    @Transactional
    public ReservationResponseDTO completeReservation(Long id) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation", "id", id));

        reservation.setStatus(ReservationStatus.COMPLETED);
        reservation.setEndTime(java.time.LocalTime.now());
        reservation.setUpdatedAt(LocalDateTime.now());
        Reservation updatedReservation = reservationRepository.save(reservation);
        eventPublisher.publishEvent(new ReservationCompletedEvent(
                this,
                updatedReservation.getId(),
                updatedReservation.getUser().getId(),
                updatedReservation.getTechnicianService().getTechnician().getId(),
                updatedReservation.getUser().getEmail()
        ));
        return mapToResponseDTO(updatedReservation);
    }

    @Transactional
    public void deleteReservation(Long id) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation", "id", id));
        reservationRepository.delete(reservation);
    }

    private ReservationResponseDTO mapToResponseDTO(Reservation reservation) {
        ReservationResponseDTO dto = new ReservationResponseDTO();

        dto.setId(reservation.getId());
        dto.setUserId(reservation.getUser().getId());
        dto.setUserName(reservation.getUser().getFirstName() + " " + reservation.getUser().getLastName());
        dto.setTechnicianId(reservation.getTechnicianService().getTechnician().getId());
        dto.setTechnicianName(reservation.getTechnicianService().getTechnician().getFirstName() + " " +
                reservation.getTechnicianService().getTechnician().getLastName());
        dto.setServiceId(reservation.getTechnicianService().getService().getId());
        dto.setServiceName(reservation.getTechnicianService().getService().getName());
        dto.setReservationDate(reservation.getReservationDate());
        dto.setServiceDate(reservation.getServiceDate());
        dto.setStartTime(reservation.getStartTime());
        dto.setEndTime(reservation.getEndTime());
        dto.setAddress(reservation.getAddress());
        dto.setStatus(reservation.getStatus());
        dto.setCreatedAt(reservation.getCreatedAt());
        dto.setUpdatedAt(reservation.getUpdatedAt());

        Double totalAmount = reservation.getPayments() != null ?
                reservation.getPayments().stream()
                        .mapToDouble(p -> p.getAmount() != null ? p.getAmount() : 0.0)
                        .sum() : 0.0;
        dto.setTotalAmount(totalAmount);

        dto.setHasReview(reservation.getReview() != null);

        return dto;
    }
}
