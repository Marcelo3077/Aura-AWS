package com.example.aura.Event.Listener;

import com.example.aura.Event.Review.ReviewCreatedEvent;
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
public class ReviewEventListener {

    private final NotificationService notificationService;
    private final AuditService auditService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Async("notificationExecutor")
    public void handleReviewCreated(ReviewCreatedEvent event) {
        log.info("⭐ Procesando evento: Reseña creada #{}", event.getReviewId());

        try {
            String stars = "⭐".repeat(event.getRating());
            String message = String.format(
                    "Has recibido una nueva reseña %s\n\n\"%s\"\n\nCalificación: %d/5 estrellas",
                    stars,
                    event.getComment() != null ? event.getComment() : "Sin comentario",
                    event.getRating()
            );

            notificationService.sendEmailNotification(
                    event.getTechnicianEmail(),
                    "⭐ Nueva Reseña Recibida - Aura",
                    message
            );

            notificationService.sendPushNotification(
                    event.getTechnicianId(),
                    "Nueva Reseña",
                    String.format("Calificación: %d/5 estrellas", event.getRating())
            );

            auditService.logUserAction(
                    event.getTechnicianId(),
                    "REVIEW_RECEIVED",
                    String.format("Review ID: %d | Rating: %d/5", event.getReviewId(), event.getRating())
            );

            log.info("✅ Notificación de reseña enviada a técnico #{}", event.getTechnicianId());

        } catch (Exception e) {
            log.error("❌ Error procesando evento de reseña creada: {}", e.getMessage());
        }
    }
}