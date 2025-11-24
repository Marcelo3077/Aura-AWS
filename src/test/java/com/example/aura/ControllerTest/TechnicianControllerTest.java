package com.example.aura.ControllerTest;

import com.example.aura.Entity.Technician.Controller.TechnicianController;
import com.example.aura.Entity.Technician.DTO.TechnicianRequestDTO;
import com.example.aura.Entity.Technician.DTO.TechnicianResponseDTO;
import com.example.aura.Entity.Technician.Service.TechnicianService;
import com.example.aura.Entity.Service.Domain.ServiceCategory;
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
import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class TechnicianControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TechnicianService technicianService;

    private TechnicianRequestDTO requestDTO;
    private TechnicianResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        requestDTO = new TechnicianRequestDTO();
        requestDTO.setFirstName("Bob");
        requestDTO.setLastName("Builder");
        requestDTO.setEmail("bob@test.com");
        requestDTO.setPassword("password123");
        requestDTO.setPhone("987654321");
        requestDTO.setDescription("Expert plumber");
        requestDTO.setSpecialties(Arrays.asList(ServiceCategory.PLUMBING));

        responseDTO = new TechnicianResponseDTO();
        responseDTO.setId(1L);
        responseDTO.setFirstName("Bob");
        responseDTO.setLastName("Builder");
        responseDTO.setEmail("bob@test.com");
        responseDTO.setPhone("987654321");
        responseDTO.setRegisterDate(LocalDate.now());
        responseDTO.setDescription("Expert plumber");
        responseDTO.setSpecialties(Arrays.asList(ServiceCategory.PLUMBING));
        responseDTO.setTotalServices(5);
        responseDTO.setTotalCertifications(2);
        responseDTO.setValidatedCertifications(1);
        responseDTO.setAverageRating(4.5);
    }

    @Test
    void sshouldCreateTechnician_whenValidRequest() throws Exception {

        when(technicianService.createTechnician(any(TechnicianRequestDTO.class)))
                .thenReturn(responseDTO);

        mockMvc.perform(post("/api/technicians")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.email").value("bob@test.com"))
                .andExpect(jsonPath("$.firstName").value("Bob"));

        verify(technicianService, times(1)).createTechnician(any(TechnicianRequestDTO.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldGetTechnicianById_whenExists() throws Exception {

        when(technicianService.getTechnicianById(1L)).thenReturn(responseDTO);

        mockMvc.perform(get("/api/technicians/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.email").value("bob@test.com"));

        verify(technicianService, times(1)).getTechnicianById(1L);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldDeleteTechnician_whenAdminRole() throws Exception {

        doNothing().when(technicianService).deleteTechnician(1L);

        mockMvc.perform(delete("/api/technicians/1"))
                .andExpect(status().isNoContent());

        verify(technicianService, times(1)).deleteTechnician(1L);
    }
}