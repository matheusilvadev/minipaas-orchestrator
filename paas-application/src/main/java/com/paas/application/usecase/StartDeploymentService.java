package com.paas.application.usecase;

import java.io.File;
import java.util.List;
import java.util.UUID;

import com.paas.application.dto.command.StartDeploymentCommand;
import com.paas.application.dto.result.DeploymentResult;
import com.paas.application.exception.ApplicationNotFoundException;
import com.paas.application.exception.DeploymentFailedException;
import com.paas.application.port.in.StartDeploymentUseCase;
import com.paas.application.port.out.ApplicationRepositoryPort;
import com.paas.application.port.out.ContainerRepositoryPort;
import com.paas.application.port.out.DeploymentRepositoryPort;
import com.paas.application.port.out.DeploymentWorkspacePort;
import com.paas.application.port.out.DockerRuntimePort;
import com.paas.application.port.out.GitClientPort;
import com.paas.application.port.out.PortAllocatorPort;
import com.paas.domain.model.Application;
import com.paas.domain.model.ContainerInstance;
import com.paas.domain.model.Deployment;
import com.paas.domain.valueobject.PortNumber;
import com.paas.domain.service.DeploymentPolicy;

public class StartDeploymentService implements StartDeploymentUseCase {
    private final ApplicationRepositoryPort applicationRepositoryPort;
    private final DeploymentRepositoryPort deploymentRepositoryPort;
    private final ContainerRepositoryPort containerRepositoryPort;
    private final PortAllocatorPort portAllocatorPort;
    private final DeploymentWorkspacePort deploymentWorkspacePort;
    private final GitClientPort gitClientPort;
    private final DockerRuntimePort dockerRuntimePort;
    private final DeploymentPolicy deploymentPolicy;

    public StartDeploymentService(ApplicationRepositoryPort applicationRepositoryPort,
            DeploymentRepositoryPort deploymentRepositoryPort,
            ContainerRepositoryPort containerRepositoryPort,
            PortAllocatorPort portAllocatorPort,
            DeploymentWorkspacePort deploymentWorkspacePort,
            GitClientPort gitClientPort,
            DockerRuntimePort dockerRuntimePort,
            DeploymentPolicy deploymentPolicy) {
        this.applicationRepositoryPort = applicationRepositoryPort;
        this.deploymentRepositoryPort = deploymentRepositoryPort;
        this.containerRepositoryPort = containerRepositoryPort;
        this.portAllocatorPort = portAllocatorPort;
        this.deploymentWorkspacePort = deploymentWorkspacePort;
        this.gitClientPort = gitClientPort;
        this.dockerRuntimePort = dockerRuntimePort;
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
        Deployment deployment = deploymentRepositoryPort.save(newDeployment);
        PortNumber allocatedPort = null;
        String containerId = null;
        boolean workspaceCreated = false;

        try {
            allocatedPort = portAllocatorPort.allocate();
            File workspace = deploymentWorkspacePort.createWorkspace(deployment.id());
            workspaceCreated = true;

            gitClientPort.clone(application.repositoryUrl(), application.branchName(), workspace);

            deployment.startBuilding();
            deployment = deploymentRepositoryPort.save(deployment);

            String imageName = buildImageName(application, deployment);
            dockerRuntimePort.buildImage(imageName, workspace);
            containerId = dockerRuntimePort.runContainer(imageName, allocatedPort.value());

            ContainerInstance containerInstance = new ContainerInstance(
                    UUID.randomUUID(),
                    deployment.id(),
                    containerId,
                    allocatedPort);
            containerRepositoryPort.save(containerInstance);

            deployment.markRunning();
            deployment = deploymentRepositoryPort.save(deployment);

            return new DeploymentResult(deployment.id(), deployment.status().name());
        } catch (RuntimeException ex) {
            handleFailure(deployment, allocatedPort, containerId, workspaceCreated);
            throw new DeploymentFailedException(
                    "Deployment failed for application " + application.id() + ": " + ex.getMessage());
        }
    }

    private String buildImageName(Application application, Deployment deployment) {
        String normalizedName = application.name()
                .trim()
                .toLowerCase()
                .replaceAll("[^a-z0-9._-]", "-");
        return normalizedName + ":" + deployment.id();
    }

    private void handleFailure(Deployment deployment, PortNumber allocatedPort, String containerId,
            boolean workspaceCreated) {
        deployment.markFailed();
        deploymentRepositoryPort.save(deployment);

        if (containerId != null) {
            try {
                dockerRuntimePort.stopContainer(containerId);
            } catch (RuntimeException ignored) {
                // Preserve the deployment failure as the main error signal.
            }
        }

        if (allocatedPort != null) {
            portAllocatorPort.release(allocatedPort);
        }

        if (workspaceCreated) {
            deploymentWorkspacePort.cleanup(deployment.id());
        }
    }
}
