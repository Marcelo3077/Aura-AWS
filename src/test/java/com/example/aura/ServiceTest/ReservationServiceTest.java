package com.example.aura.ServiceTest;

import com.example.aura.Entity.Reservation.DTO.ReservationRequestDTO;
import com.example.aura.Entity.Reservation.DTO.ReservationResponseDTO;
import com.example.aura.Entity.Reservation.Domain.Reservation;
import com.example.aura.Entity.Reservation.Domain.ReservationStatus;
import com.example.aura.Entity.Reservation.Repository.ReservationRepository;
import com.example.aura.Entity.Reservation.Service.ReservationService;
import com.example.aura.Entity.Service.Domain.Service;
import com.example.aura.Entity.Service.Domain.ServiceCategory;
import com.example.aura.Entity.Technician.Domain.Technician;
import com.example.aura.Entity.TechnicianService.Domain.TechnicianService;
import com.example.aura.Entity.TechnicianService.Domain.TechnicianServiceId;
import com.example.aura.Entity.TechnicianService.Repository.TechnicianServiceRepository;
import com.example.aura.Entity.User.Domain.User;
import com.example.aura.Entity.User.Repository.UserRepository;
import com.example.aura.Event.Reservation.ReservationConfirmedEvent;
import com.example.aura.Event.Reservation.ReservationCreatedEvent;
import com.example.aura.Exception.ResourceNotFoundException;
import com.example.aura.Service.AuditService;
import com.example.aura.Service.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReservationServiceTest {

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TechnicianServiceRepository technicianServiceRepository;

    @Mock
    private NotificationService notificationService;

    @Mock
    private AuditService auditService;

    @Mock
    private org.springframework.context.ApplicationEventPublisher eventPublisher;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private ReservationService reservationService;

    private ReservationRequestDTO requestDTO;
    private Reservation reservation;
    private User user;
    private Technician technician;
    private Service service;
    private TechnicianService technicianService;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setEmail("user@test.com");
        user.setPhone("123456789");
        user.setFirstName("John");
        user.setLastName("Doe");

        technician = new Technician();
        technician.setId(2L);
        technician.setEmail("tech@test.com");
        technician.setFirstName("Bob");
        technician.setLastName("Builder");

        service = new Service();
        service.setId(1L);
        service.setName("Plumbing");
        service.setCategory(ServiceCategory.PLUMBING);

        technicianService = new TechnicianService();
        technicianService.setId(new TechnicianServiceId(2L, 1L));
        technicianService.setTechnician(technician);
        technicianService.setService(service);
        technicianService.setBaseRate(50.0);

        requestDTO = new ReservationRequestDTO();
        requestDTO.setUserId(1L);
        requestDTO.setTechnicianId(2L);
        requestDTO.setServiceId(1L);
        requestDTO.setServiceDate(LocalDate.now().plusDays(5));
        requestDTO.setStartTime(LocalTime.of(10, 0));
        requestDTO.setAddress("123 Test St");

        reservation = new Reservation();
        reservation.setId(1L);
        reservation.setUser(user);
        reservation.setTechnicianService(technicianService);
        reservation.setReservationDate(LocalDate.now());
        reservation.setServiceDate(LocalDate.now().plusDays(5));
        reservation.setStartTime(LocalTime.of(10, 0));
        reservation.setAddress("123 Test St");
        reservation.setStatus(ReservationStatus.PENDING);
        reservation.setCreatedAt(LocalDateTime.now());
        reservation.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    void shouldThrowResourceNotFoundException_whenUserNotFound() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());
        requestDTO.setUserId(999L);

        assertThatThrownBy(() -> reservationService.createReservation(requestDTO))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("User");

        verify(reservationRepository, never()).save(any(Reservation.class));
    }

    @Test
    void shouldCreateReservation_whenAllEntitiesExist() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(technicianServiceRepository.findById(any(TechnicianServiceId.class)))
                .thenReturn(Optional.of(technicianService));
        when(reservationRepository.save(any(Reservation.class))).thenReturn(reservation);

        ReservationResponseDTO result = reservationService.createReservation(requestDTO);

        assertThat(result).isNotNull();

        verify(reservationRepository, times(1)).save(any(Reservation.class));

        verify(eventPublisher, times(1)).publishEvent(any(ReservationCreatedEvent.class));

    }

    @Test
    void shouldThrowResourceNotFoundException_whenTechnicianServiceNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(technicianServiceRepository.findById(any(TechnicianServiceId.class)))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> reservationService.createReservation(requestDTO))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("TechnicianService");

        verify(reservationRepository, never()).save(any(Reservation.class));
    }

    @Test
    void shouldConfirmReservation_whenExists() {
        when(reservationRepository.findById(1L)).thenReturn(Optional.of(reservation));
        when(reservationRepository.save(any(Reservation.class))).thenReturn(reservation);

        ReservationResponseDTO result = reservationService.confirmReservation(1L);

        assertThat(result).isNotNull();

        verify(reservationRepository, times(1)).save(any(Reservation.class));

        verify(eventPublisher, times(1)).publishEvent(any(ReservationConfirmedEvent.class));

    }

    @Test
    void shouldCancelReservation_whenExists() {
        when(reservationRepository.findById(1L)).thenReturn(Optional.of(reservation));
        when(reservationRepository.save(any(Reservation.class))).thenReturn(reservation);

        ReservationResponseDTO result = reservationService.cancelReservation(1L);

        assertThat(result).isNotNull();
        verify(reservationRepository, times(1)).save(any(Reservation.class));
    }

    @Test
    void shouldCompleteReservation_whenExists() {
        when(reservationRepository.findById(1L)).thenReturn(Optional.of(reservation));
        when(reservationRepository.save(any(Reservation.class))).thenReturn(reservation);

        ReservationResponseDTO result = reservationService.completeReservation(1L);

        assertThat(result).isNotNull();
        verify(reservationRepository, times(1)).save(any(Reservation.class));
    }
}