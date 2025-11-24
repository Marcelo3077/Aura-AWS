package com.example.aura.Exception;

public class UnauthorizedException extends RuntimeException {
    public UnauthorizedException(String message) {
        super(message);
    }

    public UnauthorizedException() {
        super("Unauthorized access - Please authenticate");
    }
}