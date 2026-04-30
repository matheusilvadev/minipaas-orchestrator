package com.paas.domain.model;

import java.util.UUID;

import com.paas.domain.exception.InvalidDeploymentStateException;

public class Deployment {
    private final UUID id;
    private final UUID applicationId;
    private DeploymentStatus status;

    // Construtor para novos Deploys (Always PENDING)
    public Deployment(UUID id, UUID applicationId) {
        this.id = id;
        this.applicationId = applicationId;
        this.status = DeploymentStatus.PENDING; // Estado inicial garantido
    }

    // Construtor privado para reconstrução
    private Deployment(UUID id, UUID applicationId, DeploymentStatus status) {
        this.id = id;
        this.applicationId = applicationId;
        this.status = status;
    }

    // Método estático de reidratação (Uso exclusivo da Infra/Testes)
    public static Deployment rehydrate(UUID id, UUID applicationId, DeploymentStatus status) {
        return new Deployment(id, applicationId, status);
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
