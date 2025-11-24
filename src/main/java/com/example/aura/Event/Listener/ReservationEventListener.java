package com.example.aura.Event.Listener;

import com.example.aura.Event.Reservation.*;
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
public class ReservationEventListener {

    private final NotificationService notificationService;
    private final AuditService auditService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Async("notificationExecutor")
    public void handleReservationCreated(ReservationCreatedEvent event) {
        log.info("📅 Procesando evento: Reserva creada #{}", event.getReservationId());

        try {
            Map<String, Object> variables = new HashMap<>();
            variables.put("reservationId", event.getReservationId());
            variables.put("serviceName", event.getServiceName());
            variables.put("serviceDate", event.getServiceDate());

            notificationService.sendEmailWithTemplate(
                    event.getUserEmail(),
                    "📅 Reserva Creada - Aura",
                    "reservation-created",
                    variables
            );

            notificationService.sendPushNotification(
                    event.getTechnicianId(),
                    "Nueva Reserva",
                    String.format("Tienes una nueva solicitud para %s", event.getServiceName())
            );

            auditService.logUserAction(
                    event.getUserId(),
                    "CREATE_RESERVATION",
                    String.format("Reservation ID: %d", event.getReservationId())
            );

            log.info("✅ Notificaciones de reserva creada enviadas");

        } catch (Exception e) {
            log.error("❌ Error procesando evento de reserva creada: {}", e.getMessage());
        }
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Async("notificationExecutor")
    public void handleReservationConfirmed(ReservationConfirmedEvent event) {
        log.info("✅ Procesando evento: Reserva confirmada #{}", event.getReservationId());

        try {
            Map<String, Object> variables = new HashMap<>();
            variables.put("reservationId", event.getReservationId());
            variables.put("technicianName", event.getTechnicianName());
            variables.put("serviceDate", event.getServiceDate());

            notificationService.sendEmailWithTemplate(
                    event.getUserEmail(),
                    "✅ ¡Reserva Confirmada! - Aura",
                    "reservation-confirmed",
                    variables
            );

            if (event.getUserPhone() != null && !event.getUserPhone().isEmpty()) {
                notificationService.sendSMSNotification(
                        event.getUserPhone(),
                        String.format("Reserva #%d confirmada. Fecha: %s",
                                event.getReservationId(), event.getServiceDate())
                );
            }

            log.info("✅ Notificaciones de confirmación enviadas");

        } catch (Exception e) {
            log.error("❌ Error procesando evento de reserva confirmada: {}", e.getMessage());
        }
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Async("notificationExecutor")
    public void handleReservationCompleted(ReservationCompletedEvent event) {
        log.info("🏁 Procesando evento: Reserva completada #{}", event.getReservationId());

        try {
            Map<String, Object> variables = new HashMap<>();
            variables.put("reservationId", event.getReservationId());

            notificationService.sendEmailWithTemplate(
                    event.getUserEmail(),
                    "⭐ ¿Cómo fue tu experiencia? - Aura",
                    "review-request",
                    variables
            );

            log.info("✅ Solicitud de reseña enviada");

        } catch (Exception e) {
            log.error("❌ Error procesando evento de reserva completada: {}", e.getMessage());
        }
    }
}