package com.paas.application.exception;

public class DeploymentNotFoundException extends RuntimeException {
    public DeploymentNotFoundException(String message) {
        super(message);
    }
}
