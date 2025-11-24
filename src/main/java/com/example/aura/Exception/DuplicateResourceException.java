package com.example.aura.Exception;

public class DuplicateResourceException extends RuntimeException {
    public DuplicateResourceException(String resourceName, String fieldName, Object fieldValue) {
        super(String.format("Duplicate %s with %s: '%s'", resourceName, fieldName, fieldValue));
    }
}