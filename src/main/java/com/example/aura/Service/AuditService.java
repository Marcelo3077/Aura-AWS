package com.example.aura.Service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@Slf4j
public class AuditService {

    @Async("taskExecutor")
    public void logUserAction(Long userId, String action, String details) {
        log.info("[AUDIT] Usuario: {} | Acción: {} | Detalles: {} | Timestamp: {}",
                userId, action, details, LocalDateTime.now());

    }

    @Async("taskExecutor")
    public void logLogin(String email, String ipAddress, boolean success) {
        log.info("[AUDIT] Login - Email: {} | IP: {} | Success: {} | Timestamp: {}",
                email, ipAddress, success, LocalDateTime.now());
    }

    @Async("taskExecutor")
    public void logEntityChange(String entityType, Long entityId, String changeType, String oldValue, String newValue) {
        log.info("[AUDIT] Entity Change - Type: {} | ID: {} | Change: {} | Old: {} | New: {} | Timestamp: {}",
                entityType, entityId, changeType, oldValue, newValue, LocalDateTime.now());
    }
}
