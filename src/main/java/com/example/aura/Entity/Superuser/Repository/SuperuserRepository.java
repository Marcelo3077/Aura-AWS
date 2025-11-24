package com.example.aura.Entity.Superuser.Repository;

import com.example.aura.Entity.Superuser.Domain.Superuser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SuperuserRepository<T extends Superuser> extends JpaRepository<T, Long> {
    Optional<T> findByEmail(String email);
}