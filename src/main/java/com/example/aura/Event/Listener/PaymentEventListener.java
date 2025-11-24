package com.example.aura.Event.Listener;

import com.example.aura.Event.Payment.PaymentCompletedEvent;
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
public class PaymentEventListener {

    private final NotificationService notificationService;
    private final AuditService auditService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Async("emailExecutor")
    public void handlePaymentCompleted(PaymentCompletedEvent event) {
        log.info("💳 Procesando evento: Pago completado #{}", event.getPaymentId());

        try {
            Map<String, Object> variables = new HashMap<>();
            variables.put("paymentId", event.getPaymentId());
            variables.put("reservationId", event.getReservationId());
            variables.put("amount", event.getAmount());
            variables.put("paymentMethod", event.getPaymentMethod());

            notificationService.sendEmailWithTemplate(
                    event.getUserEmail(),
                    "💳 Recibo de Pago - Aura",
                    "payment-receipt",
                    variables
            );

            auditService.logUserAction(
                    event.getReservationId(),
                    "PAYMENT_COMPLETED",
                    String.format("Payment ID: %d | Amount: S/. %.2f | Method: %s",
                            event.getPaymentId(), event.getAmount(), event.getPaymentMethod())
            );

            log.info("✅ Recibo de pago enviado exitosamente a {}", event.getUserEmail());

        } catch (Exception e) {
            log.error("❌ Error procesando evento de pago completado: {}", e.getMessage());
        }
    }
}