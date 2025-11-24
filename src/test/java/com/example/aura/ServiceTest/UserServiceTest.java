package com.example.aura.ServiceTest;

import com.example.aura.Entity.User.DTO.UserRequestDTO;
import com.example.aura.Entity.User.DTO.UserResponseDTO;
import com.example.aura.Entity.User.DTO.UserUpdateDTO;
import com.example.aura.Entity.User.Domain.User;
import com.example.aura.Entity.User.Repository.UserRepository;
import com.example.aura.Entity.User.Service.UserService;
import com.example.aura.Exception.ConflictException;
import com.example.aura.Exception.ResourceNotFoundException;
import com.example.aura.Security.Domain.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private UserRequestDTO requestDTO;
    private User user;
    private UserResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        requestDTO = new UserRequestDTO();
        requestDTO.setFirstName("John");
        requestDTO.setLastName("Doe");
        requestDTO.setEmail("john@test.com");
        requestDTO.setPassword("password123");
        requestDTO.setPhone("123456789");

        user = new User();
        user.setId(1L);
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setEmail("john@test.com");
        user.setPasswordHash("hashedpassword");
        user.setPhone("123456789");
        user.setRegisterDate(LocalDate.now());
        user.setRole(Role.USER);
        user.setEnabled(true);

        responseDTO = new UserResponseDTO();
        responseDTO.setId(1L);
        responseDTO.setFirstName("John");
        responseDTO.setLastName("Doe");
        responseDTO.setEmail("john@test.com");
    }

    @Test
    void shouldCreateUser_whenEmailDoesNotExist() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("hashedpassword");
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(modelMapper.map(any(User.class), eq(UserResponseDTO.class))).thenReturn(responseDTO);

        UserResponseDTO result = userService.createUser(requestDTO);

        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo("john@test.com");
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void shouldThrowConflictException_whenEmailAlreadyExists() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> userService.createUser(requestDTO))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("email");

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void shouldGetUserById_whenExists() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(modelMapper.map(any(User.class), eq(UserResponseDTO.class))).thenReturn(responseDTO);

        UserResponseDTO result = userService.getUserById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void shouldThrowResourceNotFoundException_whenUserNotFound() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getUserById(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("User not found");
    }

    @Test
    void shouldUpdateUser_whenExists() {
        UserUpdateDTO updateDTO = new UserUpdateDTO();
        updateDTO.setFirstName("Jane");
        updateDTO.setPhone("987654321");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(modelMapper.map(any(User.class), eq(UserResponseDTO.class))).thenReturn(responseDTO);

        UserResponseDTO result = userService.updateUser(1L, updateDTO);

        assertThat(result).isNotNull();
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void shouldDeleteUser_whenExists() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        doNothing().when(userRepository).delete(any(User.class));

        userService.deleteUser(1L);

        verify(userRepository, times(1)).delete(user);
    }
}