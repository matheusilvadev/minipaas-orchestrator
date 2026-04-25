package com.paas.domain.exception;

public class InvalidDeploymentStateException extends RuntimeException {
    public InvalidDeploymentStateException(String message) {
        super(message);
    }
}
