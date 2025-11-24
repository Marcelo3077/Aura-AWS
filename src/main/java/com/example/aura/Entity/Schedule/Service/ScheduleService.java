package com.example.aura.Entity.Schedule.Service;

import com.example.aura.Entity.Schedule.DTO.ScheduleRequestDTO;
import com.example.aura.Entity.Schedule.DTO.ScheduleResponseDTO;
import com.example.aura.Entity.Schedule.Domain.DayOfWeek;
import com.example.aura.Entity.Schedule.Domain.Schedule;
import com.example.aura.Entity.Schedule.Domain.ScheduleStatus;
import com.example.aura.Entity.Schedule.Repository.ScheduleRepository;
import com.example.aura.Exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final ModelMapper modelMapper;

    @Transactional
    public ScheduleResponseDTO createSchedule(ScheduleRequestDTO requestDTO) {
        Schedule schedule = new Schedule();
        schedule.setDayOfWeek(requestDTO.getDayOfWeek());
        schedule.setStartTime(requestDTO.getStartTime());
        schedule.setEndTime(requestDTO.getEndTime());
        schedule.setStatus(requestDTO.getStatus());

        Schedule savedSchedule = scheduleRepository.save(schedule);
        return mapToResponseDTO(savedSchedule);
    }

    @Transactional(readOnly = true)
    public ScheduleResponseDTO getScheduleById(Long id) {
        Schedule schedule = scheduleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Schedule", "id", id));
        return mapToResponseDTO(schedule);
    }

    @Transactional(readOnly = true)
    public List<ScheduleResponseDTO> getAllSchedules() {
        return scheduleRepository.findAll().stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ScheduleResponseDTO> getSchedulesByDayOfWeek(DayOfWeek dayOfWeek) {
        return scheduleRepository.findAll().stream()
                .filter(s -> s.getDayOfWeek() == dayOfWeek)
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ScheduleResponseDTO> getAvailableSchedules() {
        return scheduleRepository.findAll().stream()
                .filter(s -> s.getStatus() == ScheduleStatus.AVAILABLE)
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public ScheduleResponseDTO updateSchedule(Long id, ScheduleRequestDTO requestDTO) {
        Schedule schedule = scheduleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Schedule", "id", id));

        schedule.setDayOfWeek(requestDTO.getDayOfWeek());
        schedule.setStartTime(requestDTO.getStartTime());
        schedule.setEndTime(requestDTO.getEndTime());
        schedule.setStatus(requestDTO.getStatus());

        Schedule updatedSchedule = scheduleRepository.save(schedule);
        return mapToResponseDTO(updatedSchedule);
    }

    @Transactional
    public ScheduleResponseDTO updateScheduleStatus(Long id, ScheduleStatus status) {
        Schedule schedule = scheduleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Schedule", "id", id));

        schedule.setStatus(status);
        Schedule updatedSchedule = scheduleRepository.save(schedule);
        return mapToResponseDTO(updatedSchedule);
    }

    @Transactional
    public void deleteSchedule(Long id) {
        Schedule schedule = scheduleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Schedule", "id", id));
        scheduleRepository.delete(schedule);
    }

    private ScheduleResponseDTO mapToResponseDTO(Schedule schedule) {
        ScheduleResponseDTO dto = modelMapper.map(schedule, ScheduleResponseDTO.class);
        dto.setTotalTechnicians(schedule.getTechnicians() != null ? schedule.getTechnicians().size() : 0);
        return dto;
    }
}
