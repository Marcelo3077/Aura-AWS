package com.example.aura.Entity.User.Service;

import com.example.aura.Entity.User.DTO.UserRequestDTO;
import com.example.aura.Entity.User.DTO.UserResponseDTO;
import com.example.aura.Entity.User.DTO.UserUpdateDTO;
import com.example.aura.Entity.User.Domain.User;
import com.example.aura.Entity.User.Repository.UserRepository;
import com.example.aura.Exception.ConflictException;
import com.example.aura.Exception.ResourceNotFoundException;
import com.example.aura.Security.Domain.Role;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UserResponseDTO createUser(UserRequestDTO requestDTO) {
        if (userRepository.findByEmail(requestDTO.getEmail()).isPresent()) {
            throw new ConflictException("User", "email", requestDTO.getEmail());
        }

        User user = new User();
        user.setFirstName(requestDTO.getFirstName());
        user.setLastName(requestDTO.getLastName());
        user.setEmail(requestDTO.getEmail());
        user.setPasswordHash(passwordEncoder.encode(requestDTO.getPassword()));
        user.setPhone(requestDTO.getPhone());
        user.setRegisterDate(LocalDate.now());
        user.setRole(Role.USER);
        user.setEnabled(true);

        User savedUser = userRepository.save(user);
        return mapToResponseDTO(savedUser);
    }

    @Transactional(readOnly = true)
    public UserResponseDTO getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
        return mapToResponseDTO(user);
    }

    @Transactional(readOnly = true)
    public UserResponseDTO getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));
        return mapToResponseDTO(user);
    }

    @Transactional(readOnly = true)
    public List<UserResponseDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public UserResponseDTO updateUser(Long id, UserUpdateDTO updateDTO) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));

        if (updateDTO.getFirstName() != null) {
            user.setFirstName(updateDTO.getFirstName());
        }
        if (updateDTO.getLastName() != null) {
            user.setLastName(updateDTO.getLastName());
        }
        if (updateDTO.getPhone() != null) {
            user.setPhone(updateDTO.getPhone());
        }

        User updatedUser = userRepository.save(user);
        return mapToResponseDTO(updatedUser);
    }

    @Transactional
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
        userRepository.delete(user);
    }

    @Transactional(readOnly = true)
    public List<UserResponseDTO> getUsersWithReservations() {
        return userRepository.findAll().stream()
                .filter(user -> user.getReservations() != null && !user.getReservations().isEmpty())
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    private UserResponseDTO mapToResponseDTO(User user) {
        UserResponseDTO dto = modelMapper.map(user, UserResponseDTO.class);
        dto.setTotalReservations(user.getReservations() != null ? user.getReservations().size() : 0);
        return dto;
    }
}