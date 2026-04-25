package com.paas.domain.model;

import java.util.UUID;

import com.paas.domain.exception.InvalidDeploymentStateException;

public class Deployment {
    private final UUID id;
    private final UUID applicationId;
    private DeploymentStatus status;

    public Deployment(UUID id, UUID applicationId) {
        this.id = id;
        this.applicationId = applicationId;
        this.status = DeploymentStatus.PENDING; // Estado inicial garantido
    }

    public void startBuilding() {
        if (this.status != DeploymentStatus.PENDING) {
            throw new InvalidDeploymentStateException("Cannot start building from " + status);
        }
        this.status = DeploymentStatus.BUILDING;
    }

    public void markRunning() {
        if (this.status != DeploymentStatus.BUILDING) {
            throw new InvalidDeploymentStateException("Cannot mark as running if not in building status");
        }
        this.status = DeploymentStatus.RUNNING;
    }

    public void markFailed() {
        this.status = DeploymentStatus.FAILED;
    }

    public UUID id() {
        return id;
    }

    public UUID applicationId() {
        return applicationId;
    }

    public DeploymentStatus status() {
        return status;
    }

}
