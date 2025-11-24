package com.example.aura.RepositoryTest;

import com.example.aura.Entity.Reservation.Domain.Reservation;
import com.example.aura.Entity.Reservation.Domain.ReservationStatus;
import com.example.aura.Entity.Reservation.Repository.ReservationRepository;
import com.example.aura.Entity.Service.Domain.Service;
import com.example.aura.Entity.Service.Domain.ServiceCategory;
import com.example.aura.Entity.Technician.Domain.Technician;
import com.example.aura.Entity.TechnicianService.Domain.TechnicianService;
import com.example.aura.Entity.TechnicianService.Domain.TechnicianServiceId;
import com.example.aura.Entity.User.Domain.User;
import com.example.aura.Security.Domain.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class ReservationRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ReservationRepository reservationRepository;

    private User testUser;
    private Technician testTechnician;
    private Service testService;
    private TechnicianService technicianService;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setFirstName("Jane");
        testUser.setLastName("Smith");
        testUser.setEmail("jane@test.com");
        testUser.setPasswordHash("hashed");
        testUser.setRegisterDate(LocalDate.now());
        testUser.setRole(Role.USER);
        testUser.setEnabled(true);
        entityManager.persistAndFlush(testUser);


        testTechnician = new Technician();
        testTechnician.setFirstName("Bob");
        testTechnician.setLastName("Builder");
        testTechnician.setEmail("bob@test.com");
        testTechnician.setPasswordHash("hashed");
        testTechnician.setRegisterDate(LocalDate.now());
        testTechnician.setRole(Role.TECHNICIAN);
        testTechnician.setEnabled(true);
        entityManager.persistAndFlush(testTechnician);

        testService = new Service();
        testService.setName("Plumbing");
        testService.setCategory(ServiceCategory.PLUMBING);
        entityManager.persistAndFlush(testService);

        technicianService = new TechnicianService();
        technicianService.setId(new TechnicianServiceId(testTechnician.getId(), testService.getId()));
        technicianService.setTechnician(testTechnician);
        technicianService.setService(testService);
        technicianService.setBaseRate(50.0);
        entityManager.persistAndFlush(technicianService);
    }

    @Test
    void shouldSaveReservation_always() {

        Reservation reservation = createTestReservation();

        Reservation saved = reservationRepository.save(reservation);

        assertThat(saved).isNotNull();
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getStatus()).isEqualTo(ReservationStatus.PENDING);
        assertThat(saved.getUser().getEmail()).isEqualTo("jane@test.com");
    }

    @Test
    void shouldFindReservationsByUserId_whenExist() {
        Reservation reservation = createTestReservation();
        entityManager.persistAndFlush(reservation);

        List<Reservation> reservations = reservationRepository.findByUserId(testUser.getId());

        assertThat(reservations).hasSize(1);
        assertThat(reservations.get(0).getUser().getId()).isEqualTo(testUser.getId());
    }

    @Test
    void shouldFindReservationsByStatus_whenExist() {
        Reservation reservation = createTestReservation();
        entityManager.persistAndFlush(reservation);

        List<Reservation> reservations = reservationRepository.findByStatus(ReservationStatus.PENDING);

        assertThat(reservations).hasSize(1);
        assertThat(reservations.get(0).getStatus()).isEqualTo(ReservationStatus.PENDING);
    }

    @Test
    void shouldFindReservationsByTechnicianId_whenExist() {
        Reservation reservation = createTestReservation();
        entityManager.persistAndFlush(reservation);


        List<Reservation> reservations = reservationRepository.findByTechnicianServiceTechnicianId(testTechnician.getId());

        assertThat(reservations).hasSize(1);
        assertThat(reservations.get(0).getTechnicianService().getTechnician().getId()).isEqualTo(testTechnician.getId());
    }

    @Test
    void shouldFindReservationsByDateRange_whenExist() {

        Reservation reservation = createTestReservation();
        entityManager.persistAndFlush(reservation);

        LocalDate start = LocalDate.now();
        LocalDate end = LocalDate.now().plusDays(10);

        List<Reservation> reservations = reservationRepository.findByServiceDateBetween(start, end);

        assertThat(reservations).hasSize(1);
    }

    private Reservation createTestReservation() {
        Reservation reservation = new Reservation();
        reservation.setUser(testUser);
        reservation.setTechnicianService(technicianService);
        reservation.setReservationDate(LocalDate.now());
        reservation.setServiceDate(LocalDate.now().plusDays(5));
        reservation.setStartTime(LocalTime.of(10, 0));
        reservation.setAddress("123 Test St");
        reservation.setStatus(ReservationStatus.PENDING);
        reservation.setCreatedAt(LocalDateTime.now());
        reservation.setUpdatedAt(LocalDateTime.now());
        return reservation;
    }
}