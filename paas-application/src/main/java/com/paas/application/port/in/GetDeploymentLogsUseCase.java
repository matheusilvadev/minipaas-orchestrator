package com.paas.application.port.in;

import java.util.UUID;

import com.paas.application.dto.result.DeploymentLogResult;

public interface GetDeploymentLogsUseCase {
    DeploymentLogResult execute(UUID deploymentId);
}
