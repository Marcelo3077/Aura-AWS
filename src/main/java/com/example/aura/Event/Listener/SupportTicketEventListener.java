package com.example.aura.Event.Listener;

import com.example.aura.Event.SupportTicket.TicketCreatedEvent;
import com.example.aura.Service.AuditService;
import com.example.aura.Service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
@Slf4j
public class SupportTicketEventListener {

    private final NotificationService notificationService;
    private final AuditService auditService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Async("notificationExecutor")
    public void handleTicketCreated(TicketCreatedEvent event) {
        log.info("🎫 Procesando evento: Ticket creado #{}", event.getTicketId());

        try {
            String userMessage = String.format(
                    "Tu ticket de soporte ha sido creado exitosamente.\n\n" +
                            "ID del Ticket: #%d\n" +
                            "Asunto: %s\n" +
                            "Prioridad: %s\n\n" +
                            "Nuestro equipo lo revisará pronto.",
                    event.getTicketId(),
                    event.getSubject(),
                    event.getPriority()
            );

            notificationService.sendEmailNotification(
                    event.getUserEmail(),
                    "🎫 Ticket de Soporte Creado - Aura",
                    userMessage
            );

            notificationService.sendPushNotification(
                    event.getTicketId(),
                    "Ticket Creado",
                    String.format("Ticket #%d - %s", event.getTicketId(), event.getSubject())
            );

            auditService.logUserAction(
                    event.getTicketId(),
                    "TICKET_CREATED",
                    String.format("Subject: %s | Priority: %s", event.getSubject(), event.getPriority())
            );

            log.info("✅ Notificaciones de ticket enviadas para ticket #{}", event.getTicketId());

        } catch (Exception e) {
            log.error("❌ Error procesando evento de ticket creado: {}", e.getMessage());
        }
    }
}