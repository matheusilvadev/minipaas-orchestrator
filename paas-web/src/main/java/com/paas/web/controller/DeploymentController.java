package com.paas.web.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.paas.application.port.in.GetDeploymentLogsUseCase;
import com.paas.application.port.in.GetDeploymentUseCase;
import com.paas.application.port.in.ListDeploymentsUseCase;
import com.paas.application.port.in.StartDeploymentUseCase;
import com.paas.web.dto.request.StartDeploymentRequest;
import com.paas.web.dto.response.DeploymentLogsResponse;
import com.paas.web.dto.response.DeploymentResponse;
import com.paas.web.mapper.DeploymentHttpMapper;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api")
public class DeploymentController {

    private final StartDeploymentUseCase startDeploymentUseCase;
    private final ListDeploymentsUseCase listDeploymentsUseCase;
    private final GetDeploymentUseCase getDeploymentUseCase;
    private final GetDeploymentLogsUseCase getDeploymentLogsUseCase;
    private final DeploymentHttpMapper deploymentHttpMapper;

    public DeploymentController(StartDeploymentUseCase startDeploymentUseCase,
            ListDeploymentsUseCase listDeploymentsUseCase,
            GetDeploymentUseCase getDeploymentUseCase,
            GetDeploymentLogsUseCase getDeploymentLogsUseCase,
            DeploymentHttpMapper deploymentHttpMapper) {
        this.startDeploymentUseCase = startDeploymentUseCase;
        this.listDeploymentsUseCase = listDeploymentsUseCase;
        this.getDeploymentUseCase = getDeploymentUseCase;
        this.getDeploymentLogsUseCase = getDeploymentLogsUseCase;
        this.deploymentHttpMapper = deploymentHttpMapper;
    }

    @PostMapping("/deployments")
    public ResponseEntity<DeploymentResponse> create(@Valid @RequestBody StartDeploymentRequest request) {
        DeploymentResponse response = deploymentHttpMapper
                .toResponse(startDeploymentUseCase.execute(deploymentHttpMapper.toCommand(request)));
        return ResponseEntity.accepted().body(response);
    }

    @GetMapping("/deployments/{deploymentId}")
    public ResponseEntity<DeploymentResponse> getById(@PathVariable UUID deploymentId) {
        return ResponseEntity.ok(
                deploymentHttpMapper.toResponse(getDeploymentUseCase.execute(deploymentId)));
    }

    @GetMapping("/deployments/{deploymentId}/logs")
    public ResponseEntity<DeploymentLogsResponse> getLogs(@PathVariable UUID deploymentId) {
        return ResponseEntity.ok(
                deploymentHttpMapper.toLogsResponse(getDeploymentLogsUseCase.execute(deploymentId)));
    }

    @GetMapping("/applications/{applicationId}/deployments")
    public ResponseEntity<List<DeploymentResponse>> listByApplication(@PathVariable UUID applicationId) {
        List<DeploymentResponse> response = listDeploymentsUseCase.execute(applicationId).stream()
                .map(deploymentHttpMapper::toResponse)
                .toList();
        return ResponseEntity.ok(response);
    }
}
