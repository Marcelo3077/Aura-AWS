package com.example.aura.Entity.Service.Controller;

import com.example.aura.Entity.Service.DTO.ServiceRequestDTO;
import com.example.aura.Entity.Service.DTO.ServiceResponseDTO;
import com.example.aura.Entity.Service.Domain.ServiceCategory;
import com.example.aura.Entity.Service.Service.ServiceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/services")
@RequiredArgsConstructor
public class ServiceController {

    private final ServiceService serviceService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERADMIN')")
    public ResponseEntity<ServiceResponseDTO> createService(@Valid @RequestBody ServiceRequestDTO requestDTO) {
        ServiceResponseDTO response = serviceService.createService(requestDTO);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ServiceResponseDTO> getServiceById(@PathVariable Long id) {
        ServiceResponseDTO response = serviceService.getServiceById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<ServiceResponseDTO> getServiceByName(@PathVariable String name) {
        ServiceResponseDTO response = serviceService.getServiceByName(name);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<ServiceResponseDTO>> getAllServices() {
        List<ServiceResponseDTO> response = serviceService.getAllServices();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<List<ServiceResponseDTO>> getServicesByCategory(@PathVariable ServiceCategory category) {
        List<ServiceResponseDTO> response = serviceService.getServicesByCategory(category);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/count/category/{category}")
    public ResponseEntity<Long> countServicesByCategory(@PathVariable ServiceCategory category) {
        Long count = serviceService.countServicesByCategory(category);
        return ResponseEntity.ok(count);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERADMIN')")
    public ResponseEntity<ServiceResponseDTO> updateService(
            @PathVariable Long id,
            @Valid @RequestBody ServiceRequestDTO requestDTO) {
        ServiceResponseDTO response = serviceService.updateService(id, requestDTO);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERADMIN')")
    public ResponseEntity<Void> deleteService(@PathVariable Long id) {
        serviceService.deleteService(id);
        return ResponseEntity.noContent().build();
    }
}