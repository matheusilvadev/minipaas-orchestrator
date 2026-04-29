package com.paas.application.usecase;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import com.paas.application.dto.result.DeploymentResult;
import com.paas.application.port.in.ListDeploymentsUseCase;
import com.paas.application.port.out.DeploymentRepositoryPort;

public class ListDeploymentsService implements ListDeploymentsUseCase {

    private final DeploymentRepositoryPort deploymentRepositoryPort;

    public ListDeploymentsService(DeploymentRepositoryPort deploymentRepositoryPort) {
        this.deploymentRepositoryPort = deploymentRepositoryPort;
    }

    @Override
    public List<DeploymentResult> execute(UUID applicationId) {
        return deploymentRepositoryPort.findByApplicationId(applicationId).stream()
                .map(deployment -> new DeploymentResult(
                        deployment.id(),
                        deployment.status().name()))
                .collect(Collectors.toList());
    }

}
