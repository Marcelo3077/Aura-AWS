package com.example.aura.Event.User;

import com.example.aura.Event.Base.BaseEvent;
import com.example.aura.Security.Domain.Role;
import lombok.Getter;

@Getter
public class UserRegisteredEvent extends BaseEvent {

    private final Long userId;
    private final String email;
    private final String firstName;
    private final Role role;

    public UserRegisteredEvent(Object source, Long userId, String email,
                               String firstName, Role role) {
        super(source, "USER_REGISTERED");
        this.userId = userId;
        this.email = email;
        this.firstName = firstName;
        this.role = role;
    }
}