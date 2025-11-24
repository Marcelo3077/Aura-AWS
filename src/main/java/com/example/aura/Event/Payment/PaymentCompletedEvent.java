package com.example.aura.Event.Payment;

import com.example.aura.Event.Base.BaseEvent;
import lombok.Getter;

@Getter
public class PaymentCompletedEvent extends BaseEvent {

    private final Long paymentId;
    private final Long reservationId;
    private final String userEmail;
    private final Double amount;
    private final String paymentMethod;

    public PaymentCompletedEvent(Object source, Long paymentId,
                                 Long reservationId, String userEmail,
                                 Double amount, String paymentMethod) {
        super(source, "PAYMENT_COMPLETED");
        this.paymentId = paymentId;
        this.reservationId = reservationId;
        this.userEmail = userEmail;
        this.amount = amount;
        this.paymentMethod = paymentMethod;
    }
}