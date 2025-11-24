package com.example.aura.Exception;

public class InvalidOperationException extends RuntimeException {
    public InvalidOperationException(String message) {
        super(message);
    }

    public InvalidOperationException(String operation, String reason) {
        super(String.format("Invalid operation '%s': %s", operation, reason));
    }
}