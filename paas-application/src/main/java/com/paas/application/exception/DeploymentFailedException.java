package com.paas.application.exception;

public class DeploymentFailedException extends RuntimeException {
    public DeploymentFailedException(String message) {
        super(message);
    }
}
