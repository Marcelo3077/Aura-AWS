package com.example.aura.Exception;

public class BadRequestException extends RuntimeException {
    public BadRequestException(String message) {
        super(message);
    }

    public BadRequestException(String resourceName, String reason) {
        super(String.format("Bad request for %s: %s", resourceName, reason));
    }
}