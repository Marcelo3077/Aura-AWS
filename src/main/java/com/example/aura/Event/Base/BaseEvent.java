package com.example.aura.Event.Base;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.time.LocalDateTime;

@Getter
public abstract class BaseEvent extends ApplicationEvent {

    private final LocalDateTime eventTimestamp;
    private final String eventType;

    public BaseEvent(Object source, String eventType) {
        super(source);
        this.eventTimestamp = LocalDateTime.now();
        this.eventType = eventType;
    }
}