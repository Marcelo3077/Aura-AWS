package com.example.aura.Event.Reservation;

import com.example.aura.Event.Base.BaseEvent;
import lombok.Getter;

@Getter
public class ReservationCompletedEvent extends BaseEvent {
    private final Long reservationId;
    private final Long userId;
    private final Long technicianId;
    private final String userEmail;

    public ReservationCompletedEvent(Object source, Long reservationId,
                                     Long userId, Long technicianId,
                                     String userEmail) {
        super(source, "RESERVATION_COMPLETED");
        this.reservationId = reservationId;
        this.userId = userId;
        this.technicianId = technicianId;
        this.userEmail = userEmail;
    }
}