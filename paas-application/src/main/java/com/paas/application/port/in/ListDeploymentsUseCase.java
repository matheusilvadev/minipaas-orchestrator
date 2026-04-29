package com.paas.application.port.in;

import java.util.List;
import java.util.UUID;

import com.paas.application.dto.result.DeploymentResult;

public interface ListDeploymentsUseCase {
    List<DeploymentResult> execute(UUID applicationId);
}
