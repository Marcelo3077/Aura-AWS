package com.example.aura.Event.SupportTicket;

import com.example.aura.Event.Base.BaseEvent;
import com.example.aura.Entity.SupportTicket.Domain.TicketPriority;
import lombok.Getter;

@Getter
public class TicketCreatedEvent extends BaseEvent {

    private final Long ticketId;
    private final String subject;
    private final TicketPriority priority;
    private final String userEmail;

    public TicketCreatedEvent(Object source, Long ticketId, String subject,
                              TicketPriority priority, String userEmail) {
        super(source, "TICKET_CREATED");
        this.ticketId = ticketId;
        this.subject = subject;
        this.priority = priority;
        this.userEmail = userEmail;
    }
}