package com.example.aura.ControllerTest;

import com.example.aura.Security.Controller.AuthController;
import com.example.aura.Security.DTO.AuthResponseDTO;
import com.example.aura.Security.DTO.LoginRequestDTO;
import com.example.aura.Security.DTO.RegisterRequestDTO;
import com.example.aura.Security.Domain.Role;
import com.example.aura.Security.Service.AuthenticationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthenticationService authenticationService;

    private RegisterRequestDTO registerDTO;
    private LoginRequestDTO loginDTO;
    private AuthResponseDTO authResponseDTO;

    @BeforeEach
    void setUp() {
        registerDTO = new RegisterRequestDTO();
        registerDTO.setFirstName("John");
        registerDTO.setLastName("Doe");
        registerDTO.setEmail("john@test.com");
        registerDTO.setPassword("password123");
        registerDTO.setPhone("123456789");
        registerDTO.setRole(Role.USER);

        loginDTO = new LoginRequestDTO();
        loginDTO.setEmail("john@test.com");
        loginDTO.setPassword("password123");

        authResponseDTO = AuthResponseDTO.builder()
                .token("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
                .type("Bearer")
                .userId(1L)
                .email("john@test.com")
                .firstName("John")
                .lastName("Doe")
                .role(Role.USER)
                .build();
    }

    @Test
    void shouldRegisterUser_whenValidRequest() throws Exception {
        when(authenticationService.register(any(RegisterRequestDTO.class)))
                .thenReturn(authResponseDTO);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.userId").value(1L))
                .andExpect(jsonPath("$.email").value("john@test.com"))
                .andExpect(jsonPath("$.role").value("USER"));

        verify(authenticationService, times(1)).register(any(RegisterRequestDTO.class));
    }

    @Test
    void shouldReturnBadRequest_whenRegisterWithInvalidEmail() throws Exception {
        registerDTO.setEmail("invalid-email");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerDTO)))
                .andExpect(status().isBadRequest());

        verify(authenticationService, never()).register(any(RegisterRequestDTO.class));
    }

    @Test
    void shouldReturnBadRequest_whenRegisterWithShortPassword() throws Exception {

        registerDTO.setPassword("short");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerDTO)))
                .andExpect(status().isBadRequest());

        verify(authenticationService, never()).register(any(RegisterRequestDTO.class));
    }

    @Test
    void shouldLoginSuccessfully_whenValidCredentials() throws Exception {

        when(authenticationService.login(any(LoginRequestDTO.class)))
                .thenReturn(authResponseDTO);


        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.userId").value(1L))
                .andExpect(jsonPath("$.email").value("john@test.com"));

        verify(authenticationService, times(1)).login(any(LoginRequestDTO.class));
    }

    @Test
    void shouldReturnBadRequest_whenLoginWithInvalidEmail() throws Exception {

        loginDTO.setEmail("invalid-email");


        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDTO)))
                .andExpect(status().isBadRequest());

        verify(authenticationService, never()).login(any(LoginRequestDTO.class));
    }

    @Test
    void shouldReturnBadRequest_whenLoginWithEmptyPassword() throws Exception {

        loginDTO.setPassword("");


        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDTO)))
                .andExpect(status().isBadRequest());

        verify(authenticationService, never()).login(any(LoginRequestDTO.class));
    }
}