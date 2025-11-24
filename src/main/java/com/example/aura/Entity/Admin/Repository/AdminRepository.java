package com.example.aura.Entity.Admin.Repository;

import com.example.aura.Entity.Admin.Domain.AccessLevel;
import com.example.aura.Entity.Admin.Domain.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AdminRepository extends JpaRepository<Admin, Long> {
    Optional<Admin> findByEmail(String email);
    List<Admin> findByAccessLevel(AccessLevel level);
    boolean existsByEmail(String email);
}
