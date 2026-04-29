package com.paas.application.usecase;

import java.util.List;
import java.util.UUID;

import com.paas.application.dto.result.DeploymentLogResult;
import com.paas.application.exception.DeploymentNotFoundException;
import com.paas.application.port.in.GetDeploymentLogsUseCase;
import com.paas.application.port.out.DeploymentLogPort;
import com.paas.application.port.out.DeploymentRepositoryPort;

public class GetDeploymentLogsService implements GetDeploymentLogsUseCase {

    private final DeploymentRepositoryPort deploymentRepositoryPort;
    private final DeploymentLogPort deploymentLogPort;

    public GetDeploymentLogsService(DeploymentRepositoryPort deploymentRepositoryPort,
            DeploymentLogPort deploymentLogPort) {
        this.deploymentRepositoryPort = deploymentRepositoryPort;
        this.deploymentLogPort = deploymentLogPort;
    }

    @Override
    public DeploymentLogResult execute(UUID deploymentId) {
        if (!deploymentRepositoryPort.findById(deploymentId).isPresent()) {
            throw new DeploymentNotFoundException("Cannot get logs. Deployment with id: " + deploymentId + "not found");
        }

        List<String> logs = deploymentLogPort.getLogs(deploymentId);

        return new DeploymentLogResult(deploymentId, logs);
    }

}
