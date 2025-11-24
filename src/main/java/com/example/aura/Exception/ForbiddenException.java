package com.example.aura.Exception;

public class ForbiddenException extends RuntimeException {
    public ForbiddenException(String message) {
        super(message);
    }

    public ForbiddenException(String resource, String action) {
        super(String.format("Forbidden: You don't have permission to %s on %s", action, resource));
    }
}