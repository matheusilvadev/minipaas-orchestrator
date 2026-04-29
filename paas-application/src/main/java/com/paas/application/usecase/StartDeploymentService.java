package com.paas.application.usecase;

import java.util.List;
import java.util.UUID;

import com.paas.application.dto.command.StartDeploymentCommand;
import com.paas.application.dto.result.DeploymentResult;
import com.paas.application.exception.ApplicationNotFoundException;
import com.paas.application.exception.DeploymentFailedException;
import com.paas.application.port.in.StartDeploymentUseCase;
import com.paas.application.port.out.ApplicationRepositoryPort;
import com.paas.application.port.out.DeploymentRepositoryPort;
import com.paas.domain.model.Application;
import com.paas.domain.model.Deployment;
import com.paas.domain.service.DeploymentPolicy;

public class StartDeploymentService implements StartDeploymentUseCase {
    private final ApplicationRepositoryPort applicationRepositoryPort;
    private final DeploymentRepositoryPort deploymentRepositoryPort;
    private final DeploymentPolicy deploymentPolicy;

    public StartDeploymentService(ApplicationRepositoryPort applicationRepositoryPort,
            DeploymentRepositoryPort deploymentRepositoryPort, DeploymentPolicy deploymentPolicy) {
        this.applicationRepositoryPort = applicationRepositoryPort;
        this.deploymentRepositoryPort = deploymentRepositoryPort;
        this.deploymentPolicy = deploymentPolicy;
    }

    @Override
    public DeploymentResult execute(StartDeploymentCommand command) {
        Application application = applicationRepositoryPort.findById(command.applicationId())
                .orElseThrow(() -> new ApplicationNotFoundException("Application not found" + command.applicationId()));

        List<Deployment> activeDeployments = deploymentRepositoryPort.findActiveByApplicationId(application.id());

        if (!deploymentPolicy.canStartDeployment(application.id(), activeDeployments)) {
            throw new DeploymentFailedException("Cannot start deployment: an active deployment already exists.");
        }

        Deployment newDeployment = new Deployment(UUID.randomUUID(), application.id());
        Deployment savedDeployment = deploymentRepositoryPort.save(newDeployment);

        return new DeploymentResult(savedDeployment.id(), savedDeployment.status().name());
    }

}
