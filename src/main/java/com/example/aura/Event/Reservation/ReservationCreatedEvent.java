package com.example.aura.Event.Reservation;

import com.example.aura.Event.Base.BaseEvent;
import lombok.Getter;

@Getter
public class ReservationCreatedEvent extends BaseEvent {
    private final Long reservationId;
    private final Long userId;
    private final Long technicianId;
    private final String userEmail;
    private final String technicianEmail;
    private final String serviceName;
    private final String serviceDate;

    public ReservationCreatedEvent(Object source, Long reservationId, Long userId,
                                   Long technicianId, String userEmail,
                                   String technicianEmail, String serviceName,
                                   String serviceDate) {
        super(source, "RESERVATION_CREATED");
        this.reservationId = reservationId;
        this.userId = userId;
        this.technicianId = technicianId;
        this.userEmail = userEmail;
        this.technicianEmail = technicianEmail;
        this.serviceName = serviceName;
        this.serviceDate = serviceDate;
    }
}