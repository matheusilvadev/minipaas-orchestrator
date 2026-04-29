package com.paas.application.port.in;

import com.paas.application.dto.command.StartDeploymentCommand;
import com.paas.application.dto.result.DeploymentResult;

public interface StartDeploymentUseCase {
    DeploymentResult execute(StartDeploymentCommand command);
}
