package com.example.aura.Entity.Certification.Repository;

import com.example.aura.Entity.Certification.Domain.Certification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CertificationRepository extends JpaRepository<Certification, Long> {
    List<Certification> findByValidated(Boolean validated);
    List<Certification> findByTechnicianId(Long technicianId);
}
