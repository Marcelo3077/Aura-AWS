package com.example.aura.Event.Listener;

import com.example.aura.Event.User.UserRegisteredEvent;
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
public class UserEventListener {

    private final NotificationService notificationService;
    private final AuditService auditService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Async("emailExecutor")
    public void handleUserRegistered(UserRegisteredEvent event) {
        log.info("👤 Procesando evento: Usuario registrado #{}", event.getUserId());

        try {
            Map<String, Object> variables = new HashMap<>();
            variables.put("firstName", event.getFirstName());
            variables.put("email", event.getEmail());
            variables.put("role", event.getRole().toString());

            notificationService.sendEmailWithTemplate(
                    event.getEmail(),
                    "🌟 ¡Bienvenido a Aura! - Tu cuenta ha sido creada",
                    "welcome",
                    variables
            );

            notificationService.sendPushNotification(
                    event.getUserId(),
                    "¡Bienvenido a Aura!",
                    String.format("Tu cuenta %s ha sido creada exitosamente", event.getRole())
            );

            auditService.logUserAction(
                    event.getUserId(),
                    "USER_REGISTERED",
                    String.format("New %s registered: %s", event.getRole(), event.getEmail())
            );

            log.info("✅ Email de bienvenida enviado a {}", event.getEmail());

        } catch (Exception e) {
            log.error("❌ Error procesando evento de usuario registrado: {}", e.getMessage());
        }
    }
}