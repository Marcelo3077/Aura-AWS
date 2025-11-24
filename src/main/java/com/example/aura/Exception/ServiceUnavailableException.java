package com.example.aura.Exception;

public class ServiceUnavailableException extends RuntimeException {

    public ServiceUnavailableException(String message) {
        super(message);
    }

    public static ServiceUnavailableException forService(String serviceName) {
        return new ServiceUnavailableException(String.format("Service '%s' is temporarily unavailable", serviceName));
    }
}
