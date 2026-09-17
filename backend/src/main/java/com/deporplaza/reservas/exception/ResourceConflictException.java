package com.deporplaza.reservas.exception;

public class ResourceConflictException
        extends RuntimeException {

    public ResourceConflictException(String message) {
        super(message);
    }
}