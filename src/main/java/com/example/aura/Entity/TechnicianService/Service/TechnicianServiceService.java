package com.example.aura.Entity.TechnicianService.Service;

import com.example.aura.Entity.Service.Domain.Service;
import com.example.aura.Entity.Service.Repository.ServiceRepository;
import com.example.aura.Entity.Technician.Domain.Technician;
import com.example.aura.Entity.Technician.Repository.TechnicianRepository;
import com.example.aura.Entity.TechnicianService.DTO.TechnicianServiceRequestDTO;
import com.example.aura.Entity.TechnicianService.DTO.TechnicianServiceResponseDTO;
import com.example.aura.Entity.TechnicianService.Domain.TechnicianService;
import com.example.aura.Entity.TechnicianService.Domain.TechnicianServiceId;
import com.example.aura.Entity.TechnicianService.Repository.TechnicianServiceRepository;
import com.example.aura.Exception.ConflictException;
import com.example.aura.Exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@org.springframework.stereotype.Service
@RequiredArgsConstructor
public class TechnicianServiceService {

    private final TechnicianServiceRepository technicianServiceRepository;
    private final TechnicianRepository technicianRepository;
    private final ServiceRepository serviceRepository;
    private final ModelMapper modelMapper;

    @Transactional
    public TechnicianServiceResponseDTO createTechnicianService(TechnicianServiceRequestDTO requestDTO) {
        Technician technician = technicianRepository.findById(requestDTO.getTechnicianId())
                .orElseThrow(() -> new ResourceNotFoundException("Technician", "id", requestDTO.getTechnicianId()));

        Service service = serviceRepository.findById(requestDTO.getServiceId())
                .orElseThrow(() -> new ResourceNotFoundException("Service", "id", requestDTO.getServiceId()));

        TechnicianServiceId id = new TechnicianServiceId(requestDTO.getTechnicianId(), requestDTO.getServiceId());

        if (technicianServiceRepository.findById(id).isPresent()) {
            throw new ConflictException("TechnicianService", "technicianId-serviceId",
                    requestDTO.getTechnicianId() + "-" + requestDTO.getServiceId());
        }

        TechnicianService technicianService = new TechnicianService();
        technicianService.setId(id);
        technicianService.setTechnician(technician);
        technicianService.setService(service);
        technicianService.setBaseRate(requestDTO.getBaseRate());

        TechnicianService savedTS = technicianServiceRepository.save(technicianService);
        return mapToResponseDTO(savedTS);
    }

    @Transactional(readOnly = true)
    public TechnicianServiceResponseDTO getTechnicianServiceById(Long technicianId, Long serviceId) {
        TechnicianServiceId id = new TechnicianServiceId(technicianId, serviceId);
        TechnicianService ts = technicianServiceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("TechnicianService", "id",
                        technicianId + "-" + serviceId));
        return mapToResponseDTO(ts);
    }

    @Transactional(readOnly = true)
    public List<TechnicianServiceResponseDTO> getServicesByTechnicianId(Long technicianId) {
        return technicianServiceRepository.findByTechnicianId(technicianId).stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<TechnicianServiceResponseDTO> getAllTechnicianServices() {
        return technicianServiceRepository.findAll().stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public TechnicianServiceResponseDTO updateBaseRate(Long technicianId, Long serviceId, Double newBaseRate) {
        TechnicianServiceId id = new TechnicianServiceId(technicianId, serviceId);
        TechnicianService ts = technicianServiceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("TechnicianService", "id",
                        technicianId + "-" + serviceId));

        ts.setBaseRate(newBaseRate);
        TechnicianService updatedTS = technicianServiceRepository.save(ts);
        return mapToResponseDTO(updatedTS);
    }

    @Transactional
    public void deleteTechnicianService(Long technicianId, Long serviceId) {
        TechnicianServiceId id = new TechnicianServiceId(technicianId, serviceId);
        TechnicianService ts = technicianServiceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("TechnicianService", "id",
                        technicianId + "-" + serviceId));
        technicianServiceRepository.delete(ts);
    }

    private TechnicianServiceResponseDTO mapToResponseDTO(TechnicianService ts) {
        TechnicianServiceResponseDTO dto = new TechnicianServiceResponseDTO();
        dto.setTechnicianId(ts.getTechnician().getId());
        dto.setTechnicianName(ts.getTechnician().getFirstName() + " " + ts.getTechnician().getLastName());
        dto.setServiceId(ts.getService().getId());
        dto.setServiceName(ts.getService().getName());
        dto.setBaseRate(ts.getBaseRate());
        dto.setTotalReservations(ts.getReservations() != null ? ts.getReservations().size() : 0);
        return dto;
    }
}
