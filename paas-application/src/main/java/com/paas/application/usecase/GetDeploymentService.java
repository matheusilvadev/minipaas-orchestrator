package com.paas.application.usecase;

import java.util.UUID;

import com.paas.application.dto.result.DeploymentResult;
import com.paas.application.exception.DeploymentNotFoundException;
import com.paas.application.port.in.GetDeploymentUseCase;
import com.paas.application.port.out.DeploymentRepositoryPort;
import com.paas.domain.model.Deployment;

public class GetDeploymentService implements GetDeploymentUseCase {
    private final DeploymentRepositoryPort deploymentRepositoryPort;

    public GetDeploymentService(DeploymentRepositoryPort deploymentRepositoryPort) {
        this.deploymentRepositoryPort = deploymentRepositoryPort;
    }

    @Override
    public DeploymentResult execute(UUID deploymentId) {
        Deployment deployment = deploymentRepositoryPort.findById(deploymentId)
                .orElseThrow(() -> new DeploymentNotFoundException("Deployment not found with id: " + deploymentId));

        return new DeploymentResult(
                deployment.id(),
                deployment.status().name());
    }

}
