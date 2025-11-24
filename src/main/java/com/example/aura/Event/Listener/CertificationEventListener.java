package com.example.aura.Event.Listener;

import com.example.aura.Event.Certification.CertificationValidatedEvent;
import com.example.aura.Service.AuditService;
import com.example.aura.Service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class CertificationEventListener {

    private final NotificationService notificationService;
    private final AuditService auditService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Async("notificationExecutor")
    public void handleCertificationValidated(CertificationValidatedEvent event) {
        log.info("🎓 Procesando evento: Certificación validada #{}", event.getCertificationId());

        try {
            Map<String, Object> variables = new HashMap<>();
            variables.put("certificationTitle", event.getCertificationTitle());
            variables.put("adminName", event.getAdminName());

            notificationService.sendEmailWithTemplate(
                    event.getTechnicianEmail(),
                    "🏆 Certificación Validada - Aura",
                    "certification-validated",
                    variables
            );

            auditService.logUserAction(
                    event.getTechnicianId(),
                    "CERTIFICATION_VALIDATED",
                    String.format("Certification ID: %d validated by %s",
                            event.getCertificationId(), event.getAdminName())
            );

            log.info("✅ Notificación de certificación enviada exitosamente a {}",
                    event.getTechnicianEmail());

        } catch (Exception e) {
            log.error("❌ Error procesando evento de certificación validada: {}", e.getMessage());
        }
    }
}