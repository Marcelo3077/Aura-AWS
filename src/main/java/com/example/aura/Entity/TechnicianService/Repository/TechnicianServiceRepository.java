package com.example.aura.Entity.TechnicianService.Repository;

import com.example.aura.Entity.TechnicianService.Domain.TechnicianService;
import com.example.aura.Entity.TechnicianService.Domain.TechnicianServiceId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TechnicianServiceRepository extends JpaRepository<TechnicianService, TechnicianServiceId> {
    List<TechnicianService> findByTechnicianId(Long technicianId);
    List<TechnicianService> findByServiceId(Long serviceId);
    Optional<TechnicianService> findByIdTechnicianIdAndIdServiceId(Long technicianId, Long serviceId);
}
