package com.example.aura.Entity.Service.Repository;

import com.example.aura.Entity.Service.Domain.Service;
import com.example.aura.Entity.Service.Domain.ServiceCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ServiceRepository extends JpaRepository<Service, Long> {
    List<Service> findByCategory(ServiceCategory category);
    Long countByCategory(ServiceCategory category);
    Optional<Service> findByName(String name);
    boolean existsByName(String name);
}
