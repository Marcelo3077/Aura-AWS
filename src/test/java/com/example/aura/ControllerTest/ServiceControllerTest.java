package com.example.aura.ControllerTest;

import com.example.aura.Entity.Service.Controller.ServiceController;
import com.example.aura.Entity.Service.DTO.ServiceRequestDTO;
import com.example.aura.Entity.Service.DTO.ServiceResponseDTO;
import com.example.aura.Entity.Service.Domain.ServiceCategory;
import com.example.aura.Entity.Service.Service.ServiceService;
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

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ServiceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ServiceService serviceService;

    private ServiceRequestDTO requestDTO;
    private ServiceResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        requestDTO = new ServiceRequestDTO();
        requestDTO.setName("Plumbing Repair");
        requestDTO.setDescription("Professional plumbing services");
        requestDTO.setCategory(ServiceCategory.PLUMBING);

        responseDTO = new ServiceResponseDTO();
        responseDTO.setId(1L);
        responseDTO.setName("Plumbing Repair");
        responseDTO.setDescription("Professional plumbing services");
        responseDTO.setCategory(ServiceCategory.PLUMBING);
        responseDTO.setTotalTechnicians(5);
        responseDTO.setTotalReservations(20);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldCreateService_whenAdminRole() throws Exception {

        when(serviceService.createService(any(ServiceRequestDTO.class)))
                .thenReturn(responseDTO);

        mockMvc.perform(post("/api/services")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Plumbing Repair"))
                .andExpect(jsonPath("$.category").value("PLUMBING"));

        verify(serviceService, times(1)).createService(any(ServiceRequestDTO.class));
    }

    @Test
    void shouldGetServiceById_whenExists() throws Exception {

        when(serviceService.getServiceById(1L)).thenReturn(responseDTO);

        mockMvc.perform(get("/api/services/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Plumbing Repair"));

        verify(serviceService, times(1)).getServiceById(1L);
    }

    @Test
    void shouldGetAllServices_always() throws Exception {

        List<ServiceResponseDTO> services = Arrays.asList(responseDTO);
        when(serviceService.getAllServices()).thenReturn(services);

        mockMvc.perform(get("/api/services"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name").value("Plumbing Repair"));

        verify(serviceService, times(1)).getAllServices();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldDeleteService_whenAdminRole() throws Exception {

        doNothing().when(serviceService).deleteService(1L);

        mockMvc.perform(delete("/api/services/1"))
                .andExpect(status().isNoContent());

        verify(serviceService, times(1)).deleteService(1L);
    }
}