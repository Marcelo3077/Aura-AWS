package com.example.aura.Entity.Technician.Service;

import com.example.aura.Entity.Certification.Domain.Certification;
import com.example.aura.Entity.Schedule.Domain.DayOfWeek;
import com.example.aura.Entity.Schedule.Domain.Schedule;
import com.example.aura.Entity.Schedule.Repository.ScheduleRepository;
import com.example.aura.Entity.Service.Domain.ServiceCategory;
import com.example.aura.Entity.Technician.DTO.TechnicianRequestDTO;
import com.example.aura.Entity.Technician.DTO.TechnicianResponseDTO;
import com.example.aura.Entity.Technician.DTO.TechnicianUpdateDTO;
import com.example.aura.Entity.Technician.Domain.Technician;
import com.example.aura.Entity.Technician.Repository.TechnicianRepository;
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
public class TechnicianService {

    private final TechnicianRepository technicianRepository;
    private final ScheduleRepository scheduleRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public TechnicianResponseDTO createTechnician(TechnicianRequestDTO requestDTO) {
        if (technicianRepository.findByEmail(requestDTO.getEmail()).isPresent()) {
            throw new ConflictException("Technician", "email", requestDTO.getEmail());
        }

        Technician technician = new Technician();
        technician.setFirstName(requestDTO.getFirstName());
        technician.setLastName(requestDTO.getLastName());
        technician.setEmail(requestDTO.getEmail());
        technician.setPasswordHash(passwordEncoder.encode(requestDTO.getPassword()));
        technician.setPhone(requestDTO.getPhone());
        technician.setRegisterDate(LocalDate.now());
        technician.setDescription(requestDTO.getDescription());
        technician.setSpecialties(requestDTO.getSpecialties());
        technician.setRole(Role.TECHNICIAN);
        technician.setEnabled(true);

        Technician savedTechnician = technicianRepository.save(technician);
        return mapToResponseDTO(savedTechnician);
    }

    @Transactional(readOnly = true)
    public TechnicianResponseDTO getTechnicianById(Long id) {
        Technician technician = technicianRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Technician", "id", id));
        return mapToResponseDTO(technician);
    }

    @Transactional(readOnly = true)
    public TechnicianResponseDTO getTechnicianByEmail(String email) {
        Technician technician = technicianRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Technician", "email", email));
        return mapToResponseDTO(technician);
    }

    @Transactional(readOnly = true)
    public List<TechnicianResponseDTO> getAllTechnicians() {
        return technicianRepository.findAll().stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<TechnicianResponseDTO> getTechniciansBySpecialty(ServiceCategory specialty) {
        return technicianRepository.findBySpecialty(specialty).stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<TechnicianResponseDTO> getTechniciansByFirstName(String firstName) {
        return technicianRepository.findByFirstName(firstName).stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<TechnicianResponseDTO> getAvailableTechniciansByDay(DayOfWeek dayOfWeek) {
        return technicianRepository.findAvailableTechniciansByDay(dayOfWeek).stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<TechnicianResponseDTO> getCertifiedTechnicians() {
        return technicianRepository.findCertifiedTechnicians().stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Long countTechniciansBySpecialty(ServiceCategory specialty) {
        return technicianRepository.countBySpecialty(specialty);
    }

    @Transactional
    public TechnicianResponseDTO updateTechnician(Long id, TechnicianUpdateDTO updateDTO) {
        Technician technician = technicianRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Technician", "id", id));

        if (updateDTO.getFirstName() != null) {
            technician.setFirstName(updateDTO.getFirstName());
        }
        if (updateDTO.getLastName() != null) {
            technician.setLastName(updateDTO.getLastName());
        }
        if (updateDTO.getPhone() != null) {
            technician.setPhone(updateDTO.getPhone());
        }
        if (updateDTO.getDescription() != null) {
            technician.setDescription(updateDTO.getDescription());
        }
        if (updateDTO.getSpecialties() != null) {
            technician.setSpecialties(updateDTO.getSpecialties());
        }

        Technician updatedTechnician = technicianRepository.save(technician);
        return mapToResponseDTO(updatedTechnician);
    }

    @Transactional
    public void deleteTechnician(Long id) {
        Technician technician = technicianRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Technician", "id", id));
        technicianRepository.delete(technician);
    }

    @Transactional
    public TechnicianResponseDTO addScheduleToTechnician(Long technicianId, Long scheduleId) {
        Technician technician = technicianRepository.findById(technicianId)
                .orElseThrow(() -> new ResourceNotFoundException("Technician", "id", technicianId));

        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new ResourceNotFoundException("Schedule", "id", scheduleId));

        if (!technician.getSchedules().contains(schedule)) {
            technician.getSchedules().add(schedule);
            schedule.getTechnicians().add(technician);
            technicianRepository.save(technician);
        }

        return mapToResponseDTO(technician);
    }

    @Transactional
    public TechnicianResponseDTO removeScheduleFromTechnician(Long technicianId, Long scheduleId) {
        Technician technician = technicianRepository.findById(technicianId)
                .orElseThrow(() -> new ResourceNotFoundException("Technician", "id", technicianId));

        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new ResourceNotFoundException("Schedule", "id", scheduleId));

        technician.getSchedules().remove(schedule);
        schedule.getTechnicians().remove(technician);
        technicianRepository.save(technician);

        return mapToResponseDTO(technician);
    }

    private TechnicianResponseDTO mapToResponseDTO(Technician technician) {
        TechnicianResponseDTO dto = modelMapper.map(technician, TechnicianResponseDTO.class);

        dto.setTotalServices(technician.getTechnicianServices() != null ?
                technician.getTechnicianServices().size() : 0);

        dto.setTotalCertifications(technician.getCertifications() != null ?
                technician.getCertifications().size() : 0);

        long validatedCount = technician.getCertifications() != null ?
                technician.getCertifications().stream()
                        .filter(Certification::getValidated)
                        .count() : 0;
        dto.setValidatedCertifications((int) validatedCount);

        dto.setAverageRating(calculateAverageRating(technician));

        return dto;
    }

    private Double calculateAverageRating(Technician technician) {
        if (technician.getTechnicianServices() == null || technician.getTechnicianServices().isEmpty()) {
            return 0.0;
        }

        double totalRating = 0.0;
        int count = 0;

        for (var ts : technician.getTechnicianServices()) {
            if (ts.getReservations() != null) {
                for (var reservation : ts.getReservations()) {
                    if (reservation.getReview() != null && reservation.getReview().getRating() != null) {
                        totalRating += reservation.getReview().getRating();
                        count++;
                    }
                }
            }
        }

        return count > 0 ? Math.round((totalRating / count) * 10.0) / 10.0 : 0.0;
    }

    private String encryptPassword(String password) {
        return password;
    }
}
