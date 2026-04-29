package com.paas.application.port.in;

import java.util.UUID;

import com.paas.application.dto.result.DeploymentResult;

public interface GetDeploymentUseCase {
    DeploymentResult execute(UUID deploymentId);
}
