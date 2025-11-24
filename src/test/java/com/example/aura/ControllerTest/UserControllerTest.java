package com.example.aura.ControllerTest;

import com.example.aura.Entity.User.Controller.UserController;
import com.example.aura.Entity.User.DTO.UserRequestDTO;
import com.example.aura.Entity.User.DTO.UserResponseDTO;
import com.example.aura.Entity.User.Service.UserService;
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
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    private UserRequestDTO requestDTO;
    private UserResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        requestDTO = new UserRequestDTO();
        requestDTO.setFirstName("John");
        requestDTO.setLastName("Doe");
        requestDTO.setEmail("john@test.com");
        requestDTO.setPassword("password123");
        requestDTO.setPhone("123456789");

        responseDTO = new UserResponseDTO();
        responseDTO.setId(1L);
        responseDTO.setFirstName("John");
        responseDTO.setLastName("Doe");
        responseDTO.setEmail("john@test.com");
        responseDTO.setPhone("123456789");
        responseDTO.setRegisterDate(LocalDate.now());
        responseDTO.setTotalReservations(0);
    }

    @Test
    void shouldCreateUser_whenValidRequest() throws Exception {

        when(userService.createUser(any(UserRequestDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.email").value("john@test.com"))
                .andExpect(jsonPath("$.firstName").value("John"));

        verify(userService, times(1)).createUser(any(UserRequestDTO.class));
    }

    @Test
    void shouldReturnBadRequest_whenInvalidEmail() throws Exception {

        requestDTO.setEmail("invalid-email");


        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isBadRequest());

        verify(userService, never()).createUser(any(UserRequestDTO.class));
    }

    @Test
    void shouldReturnBadRequest_whenPasswordTooShort() throws Exception {

        requestDTO.setPassword("short");

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isBadRequest());

        verify(userService, never()).createUser(any(UserRequestDTO.class));
    }

    @Test
    @WithMockUser(roles = "USER")
    void shouldGetUserById_whenExists() throws Exception {

        when(userService.getUserById(1L)).thenReturn(responseDTO);

        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.email").value("john@test.com"));

        verify(userService, times(1)).getUserById(1L);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldGetAllUsers_whenAdminRole() throws Exception {

        List<UserResponseDTO> users = Arrays.asList(responseDTO);
        when(userService.getAllUsers()).thenReturn(users);

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].email").value("john@test.com"));

        verify(userService, times(1)).getAllUsers();
    }

    @Test
    @WithMockUser(roles = "USER")
    void shouldDeleteUser_whenExists() throws Exception {

        doNothing().when(userService).deleteUser(1L);

        mockMvc.perform(delete("/api/users/1"))
                .andExpect(status().isNoContent());

        verify(userService, times(1)).deleteUser(1L);
    }

}