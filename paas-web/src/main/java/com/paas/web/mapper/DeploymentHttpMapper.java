package com.paas.web.mapper;

import org.springframework.stereotype.Component;

import com.paas.application.dto.command.StartDeploymentCommand;
import com.paas.application.dto.result.DeploymentLogResult;
import com.paas.application.dto.result.DeploymentResult;
import com.paas.web.dto.request.StartDeploymentRequest;
import com.paas.web.dto.response.DeploymentLogsResponse;
import com.paas.web.dto.response.DeploymentResponse;

@Component
public class DeploymentHttpMapper {

    public StartDeploymentCommand toCommand(StartDeploymentRequest request) {
        return new StartDeploymentCommand(request.applicationId());
    }

    public DeploymentResponse toResponse(DeploymentResult result) {
        return new DeploymentResponse(result.id(), result.status());
    }

    public DeploymentLogsResponse toLogsResponse(DeploymentLogResult result) {
        return new DeploymentLogsResponse(result.deploymentId(), result.lines());
    }
}
