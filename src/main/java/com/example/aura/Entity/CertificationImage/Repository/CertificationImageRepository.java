package com.example.aura.Entity.CertificationImage.Repository;

import com.example.aura.Entity.CertificationImage.Domain.CertificationImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CertificationImageRepository extends JpaRepository<CertificationImage, Long> {
    List<CertificationImage> findByCertificationId(Long certificationId);
}
