package com.example.aura.ControllerTest;

import com.example.aura.Entity.Reservation.Controller.ReservationController;
import com.example.aura.Entity.Reservation.DTO.ReservationRequestDTO;
import com.example.aura.Entity.Reservation.DTO.ReservationResponseDTO;
import com.example.aura.Entity.Reservation.Domain.ReservationStatus;
import com.example.aura.Entity.Reservation.Service.ReservationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ReservationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ReservationService reservationService;

    private ReservationRequestDTO requestDTO;
    private ReservationResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        requestDTO = new ReservationRequestDTO();
        requestDTO.setUserId(1L);
        requestDTO.setTechnicianId(2L);
        requestDTO.setServiceId(1L);
        requestDTO.setServiceDate(LocalDate.now().plusDays(5));
        requestDTO.setStartTime(LocalTime.of(10, 0));
        requestDTO.setAddress("123 Test St");

        responseDTO = new ReservationResponseDTO();
        responseDTO.setId(1L);
        responseDTO.setUserId(1L);
        responseDTO.setUserName("John Doe");
        responseDTO.setTechnicianId(2L);
        responseDTO.setTechnicianName("Bob Builder");
        responseDTO.setServiceId(1L);
        responseDTO.setServiceName("Plumbing");
        responseDTO.setReservationDate(LocalDate.now());
        responseDTO.setServiceDate(LocalDate.now().plusDays(5));
        responseDTO.setStartTime(LocalTime.of(10, 0));
        responseDTO.setAddress("123 Test St");
        responseDTO.setStatus(ReservationStatus.PENDING);
        responseDTO.setCreatedAt(LocalDateTime.now());
        responseDTO.setUpdatedAt(LocalDateTime.now());
        responseDTO.setTotalAmount(50.0);
        responseDTO.setHasReview(false);
    }

    @Test
    @WithMockUser(roles = "USER")
    void sshouldCreateReservation_whenValidRequest() throws Exception {

        when(reservationService.createReservation(any(ReservationRequestDTO.class)))
                .thenReturn(responseDTO);


        mockMvc.perform(post("/api/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.status").value("PENDING"))
                .andExpect(jsonPath("$.address").value("123 Test St"));

        verify(reservationService, times(1)).createReservation(any(ReservationRequestDTO.class));
    }

    @Test
    @WithMockUser(roles = "USER")
    void shouldReturnBadRequest_whenServiceDateInPast() throws Exception {

        requestDTO.setServiceDate(LocalDate.now().minusDays(1));


        mockMvc.perform(post("/api/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isBadRequest());

        verify(reservationService, never()).createReservation(any(ReservationRequestDTO.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldGetReservationById_whenExists() throws Exception {

        when(reservationService.getReservationById(1L)).thenReturn(responseDTO);


        mockMvc.perform(get("/api/reservations/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.status").value("PENDING"));

        verify(reservationService, times(1)).getReservationById(1L);
    }

    @Test
    @WithMockUser(roles = "TECHNICIAN")
    void shouldConfirmReservation_whenTechnicianRole() throws Exception {

        responseDTO.setStatus(ReservationStatus.CONFIRMED);
        when(reservationService.confirmReservation(1L)).thenReturn(responseDTO);


        mockMvc.perform(patch("/api/reservations/1/confirm"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CONFIRMED"));

        verify(reservationService, times(1)).confirmReservation(1L);
    }

    @Test
    @WithMockUser(roles = "USER")
    void shouldCancelReservation_whenUserRole() throws Exception {

        responseDTO.setStatus(ReservationStatus.CANCELLED);
        when(reservationService.cancelReservation(1L)).thenReturn(responseDTO);


        mockMvc.perform(patch("/api/reservations/1/cancel"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CANCELLED"));

        verify(reservationService, times(1)).cancelReservation(1L);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldDeleteReservation_whenAdminRole() throws Exception {

        doNothing().when(reservationService).deleteReservation(1L);


        mockMvc.perform(delete("/api/reservations/1"))
                .andExpect(status().isNoContent());

        verify(reservationService, times(1)).deleteReservation(1L);
    }

    @Test
    void shouldReturnUnauthorized_whenNoAuthentication() throws Exception {

        mockMvc.perform(post("/api/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isForbidden());
    }
}