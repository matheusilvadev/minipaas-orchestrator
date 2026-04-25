package com.paas.domain.exception;

public class InvalidRepositoryException extends RuntimeException {

    public InvalidRepositoryException(String message) {
        super(message);
    }
}
