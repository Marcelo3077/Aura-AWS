package com.example.aura.TestContainer;

import com.example.aura.Entity.Reservation.DTO.ReservationRequestDTO;
import com.example.aura.Entity.Reservation.DTO.ReservationResponseDTO;
import com.example.aura.Entity.Reservation.Domain.ReservationStatus;
import com.example.aura.Entity.Reservation.Service.ReservationService;
import com.example.aura.Entity.Service.Domain.Service;
import com.example.aura.Entity.Service.Domain.ServiceCategory;
import com.example.aura.Entity.Service.Repository.ServiceRepository;
import com.example.aura.Entity.Technician.Domain.Technician;
import com.example.aura.Entity.Technician.Repository.TechnicianRepository;
import com.example.aura.Entity.TechnicianService.Domain.TechnicianService;
import com.example.aura.Entity.TechnicianService.Domain.TechnicianServiceId;
import com.example.aura.Entity.TechnicianService.Repository.TechnicianServiceRepository;
import com.example.aura.Entity.User.Domain.User;
import com.example.aura.Entity.User.Repository.UserRepository;
import com.example.aura.Security.Domain.Role;
import com.example.aura.TestContainersConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class ReservationServiceIntegrationTest extends TestContainersConfig {

    @Autowired
    private ReservationService reservationService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TechnicianRepository technicianRepository;

    @Autowired
    private ServiceRepository serviceRepository;

    @Autowired
    private TechnicianServiceRepository technicianServiceRepository;

    private User user;
    private Technician technician;
    private Service service;
    private TechnicianService technicianService;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setFirstName("Test");
        user.setLastName("User");
        user.setEmail("testuser@integration.com");
        user.setPasswordHash("hashed");
        user.setRegisterDate(LocalDate.now());
        user.setRole(Role.USER);
        user.setEnabled(true);
        user = userRepository.save(user);

        technician = new Technician();
        technician.setFirstName("Test");
        technician.setLastName("Technician");
        technician.setEmail("testtech@integration.com");
        technician.setPasswordHash("hashed");
        technician.setRegisterDate(LocalDate.now());
        technician.setRole(Role.TECHNICIAN);
        technician.setEnabled(true);
        technician.setDescription("Test technician");
        technician = technicianRepository.save(technician);

        service = new Service();
        service.setName("Integration Test Service");
        service.setDescription("Test service");
        service.setCategory(ServiceCategory.PLUMBING);
        service = serviceRepository.save(service);

        technicianService = new TechnicianService();
        technicianService.setId(new TechnicianServiceId(technician.getId(), service.getId()));
        technicianService.setTechnician(technician);
        technicianService.setService(service);
        technicianService.setBaseRate(100.0);
        technicianService = technicianServiceRepository.save(technicianService);
    }

    @Test
    void shouldCreateAndRetrieveReservation_fromDatabase() {
        ReservationRequestDTO requestDTO = new ReservationRequestDTO();
        requestDTO.setUserId(user.getId());
        requestDTO.setTechnicianId(technician.getId());
        requestDTO.setServiceId(service.getId());
        requestDTO.setServiceDate(LocalDate.now().plusDays(5));
        requestDTO.setStartTime(LocalTime.of(10, 0));
        requestDTO.setAddress("123 Integration Test St");

        ReservationResponseDTO created = reservationService.createReservation(requestDTO);

        assertThat(created).isNotNull();
        assertThat(created.getId()).isNotNull();
        assertThat(created.getStatus()).isEqualTo(ReservationStatus.PENDING);
        assertThat(created.getAddress()).isEqualTo("123 Integration Test St");

        ReservationResponseDTO retrieved = reservationService.getReservationById(created.getId());
        assertThat(retrieved).isNotNull();
        assertThat(retrieved.getId()).isEqualTo(created.getId());
    }

    @Test
    void shouldConfirmReservationAndPersistStatusChange_successfully() {
        ReservationRequestDTO requestDTO = new ReservationRequestDTO();
        requestDTO.setUserId(user.getId());
        requestDTO.setTechnicianId(technician.getId());
        requestDTO.setServiceId(service.getId());
        requestDTO.setServiceDate(LocalDate.now().plusDays(5));
        requestDTO.setStartTime(LocalTime.of(14, 0));
        requestDTO.setAddress("456 Test Ave");

        ReservationResponseDTO created = reservationService.createReservation(requestDTO);

        ReservationResponseDTO confirmed = reservationService.confirmReservation(created.getId());

        assertThat(confirmed.getStatus()).isEqualTo(ReservationStatus.CONFIRMED);

        ReservationResponseDTO retrieved = reservationService.getReservationById(created.getId());
        assertThat(retrieved.getStatus()).isEqualTo(ReservationStatus.CONFIRMED);
    }

    @Test
    void shouldCancelReservationAndPersistStatusChange_successfully() {
        ReservationRequestDTO requestDTO = new ReservationRequestDTO();
        requestDTO.setUserId(user.getId());
        requestDTO.setTechnicianId(technician.getId());
        requestDTO.setServiceId(service.getId());
        requestDTO.setServiceDate(LocalDate.now().plusDays(7));
        requestDTO.setStartTime(LocalTime.of(16, 0));
        requestDTO.setAddress("789 Cancel St");

        ReservationResponseDTO created = reservationService.createReservation(requestDTO);

        ReservationResponseDTO cancelled = reservationService.cancelReservation(created.getId());

        assertThat(cancelled.getStatus()).isEqualTo(ReservationStatus.CANCELLED);
    }
}