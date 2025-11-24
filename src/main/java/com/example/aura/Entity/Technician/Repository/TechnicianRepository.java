package com.example.aura.Entity.Technician.Repository;

import com.example.aura.Entity.Schedule.Domain.DayOfWeek;
import com.example.aura.Entity.Service.Domain.ServiceCategory;
import com.example.aura.Entity.Technician.Domain.Technician;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TechnicianRepository extends JpaRepository<Technician, Long> {
    Optional<Technician> findByEmail(String email);
    boolean existsByEmail(String email);

    @Query("SELECT t FROM Technician t JOIN t.specialties s WHERE s = :specialty")
    List<Technician> findBySpecialty(@Param("specialty") ServiceCategory specialty);

    List<Technician> findByFirstName(String firstName);

    @Query("SELECT COUNT(t) FROM Technician t JOIN t.specialties s WHERE s = :specialty")
    Long countBySpecialty(@Param("specialty") ServiceCategory specialty);

    @Query("SELECT t FROM Technician t JOIN t.schedules s " +
            "WHERE s.dayOfWeek = :dayOfWeek AND s.status = 'AVAILABLE'")
    List<Technician> findAvailableTechniciansByDay(@Param("dayOfWeek") DayOfWeek dayOfWeek);

    @Query("SELECT DISTINCT t FROM Technician t JOIN t.certifications c WHERE c.validated = true")
    List<Technician> findCertifiedTechnicians();
}
