package com.example.aura.Entity.Service.Service;

import com.example.aura.Entity.Service.DTO.ServiceRequestDTO;
import com.example.aura.Entity.Service.DTO.ServiceResponseDTO;
import com.example.aura.Entity.Service.Domain.Service;
import com.example.aura.Entity.Service.Domain.ServiceCategory;
import com.example.aura.Entity.Service.Repository.ServiceRepository;
import com.example.aura.Exception.ConflictException;
import com.example.aura.Exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@org.springframework.stereotype.Service
@RequiredArgsConstructor
public class ServiceService {

    private final ServiceRepository serviceRepository;
    private final ModelMapper modelMapper;

    @Transactional
    public ServiceResponseDTO createService(ServiceRequestDTO requestDTO) {
        if (serviceRepository.findByName(requestDTO.getName()).isPresent()) {
            throw new ConflictException("Service", "name", requestDTO.getName());
        }

        Service service = new Service();
        service.setName(requestDTO.getName());
        service.setDescription(requestDTO.getDescription());
        service.setCategory(requestDTO.getCategory());

        Service savedService = serviceRepository.save(service);
        return mapToResponseDTO(savedService);
    }

    @Transactional(readOnly = true)
    public ServiceResponseDTO getServiceById(Long id) {
        Service service = serviceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Service", "id", id));
        return mapToResponseDTO(service);
    }

    @Transactional(readOnly = true)
    public ServiceResponseDTO getServiceByName(String name) {
        Service service = serviceRepository.findByName(name)
                .orElseThrow(() -> new ResourceNotFoundException("Service", "name", name));
        return mapToResponseDTO(service);
    }

    @Transactional(readOnly = true)
    public List<ServiceResponseDTO> getAllServices() {
        return serviceRepository.findAll().stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ServiceResponseDTO> getServicesByCategory(ServiceCategory category) {
        return serviceRepository.findByCategory(category).stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Long countServicesByCategory(ServiceCategory category) {
        return serviceRepository.countByCategory(category);
    }

    @Transactional
    public ServiceResponseDTO updateService(Long id, ServiceRequestDTO requestDTO) {
        Service service = serviceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Service", "id", id));

        if (!service.getName().equals(requestDTO.getName())) {
            if (serviceRepository.findByName(requestDTO.getName()).isPresent()) {
                throw new ConflictException("Service", "name", requestDTO.getName());
            }
        }

        service.setName(requestDTO.getName());
        service.setDescription(requestDTO.getDescription());
        service.setCategory(requestDTO.getCategory());

        Service updatedService = serviceRepository.save(service);
        return mapToResponseDTO(updatedService);
    }

    @Transactional
    public void deleteService(Long id) {
        Service service = serviceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Service", "id", id));
        serviceRepository.delete(service);
    }

    private ServiceResponseDTO mapToResponseDTO(Service service) {
        ServiceResponseDTO dto = modelMapper.map(service, ServiceResponseDTO.class);

        dto.setTotalTechnicians(service.getTechnicianServices() != null ?
                service.getTechnicianServices().size() : 0);

        int totalReservations = 0;
        if (service.getTechnicianServices() != null) {
            for (var ts : service.getTechnicianServices()) {
                if (ts.getReservations() != null) {
                    totalReservations += ts.getReservations().size();
                }
            }
        }
        dto.setTotalReservations(totalReservations);

        return dto;
    }
}
