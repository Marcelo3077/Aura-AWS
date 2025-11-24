package com.example.aura.Event.Reservation;

import com.example.aura.Event.Base.BaseEvent;
import lombok.Getter;

@Getter
public class ReservationConfirmedEvent extends BaseEvent {
    private final Long reservationId;
    private final String userEmail;
    private final String userPhone;
    private final String technicianName;
    private final String serviceDate;

    public ReservationConfirmedEvent(Object source, Long reservationId,
                                     String userEmail, String userPhone,
                                     String technicianName, String serviceDate) {
        super(source, "RESERVATION_CONFIRMED");
        this.reservationId = reservationId;
        this.userEmail = userEmail;
        this.userPhone = userPhone;
        this.technicianName = technicianName;
        this.serviceDate = serviceDate;
    }
}