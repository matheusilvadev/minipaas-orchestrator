package com.paas.bootstrap;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.paas.application.port.in.CreateApplicationUseCase;
import com.paas.application.port.in.GetApplicationUseCase;
import com.paas.application.port.in.GetDeploymentLogsUseCase;
import com.paas.application.port.in.GetDeploymentUseCase;
import com.paas.application.port.in.ListApplicationsUseCase;
import com.paas.application.port.in.ListContainersUseCase;
import com.paas.application.port.in.ListDeploymentsUseCase;
import com.paas.application.port.in.StartDeploymentUseCase;
import com.paas.application.port.in.StopContainerUseCase;
import com.paas.application.port.out.ApplicationRepositoryPort;
import com.paas.application.port.out.ContainerRepositoryPort;
import com.paas.application.port.out.DeploymentLogPort;
import com.paas.application.port.out.DeploymentLogPublisherPort;
import com.paas.application.port.out.DeploymentRepositoryPort;
import com.paas.application.port.out.DeploymentWorkspacePort;
import com.paas.application.port.out.DockerRuntimePort;
import com.paas.application.port.out.GitClientPort;
import com.paas.application.port.out.PortAllocatorPort;
import com.paas.application.usecase.CreateApplicationService;
import com.paas.application.usecase.GetApplicationService;
import com.paas.application.usecase.GetDeploymentLogsService;
import com.paas.application.usecase.GetDeploymentService;
import com.paas.application.usecase.ListApplicationsService;
import com.paas.application.usecase.ListContainersService;
import com.paas.application.usecase.ListDeploymentsService;
import com.paas.application.usecase.StartDeploymentService;
import com.paas.application.usecase.StopContainerService;
import com.paas.domain.service.DeploymentPolicy;

@Configuration
public class BootstrapConfig {

    @Bean
    public DeploymentPolicy deploymentPolicy() {
        return new DeploymentPolicy();
    }

    @Bean
    public CreateApplicationUseCase createApplicationUseCase(ApplicationRepositoryPort applicationRepositoryPort) {
        return new CreateApplicationService(applicationRepositoryPort);
    }

    @Bean
    public ListApplicationsUseCase listApplicationsUseCase(ApplicationRepositoryPort applicationRepositoryPort) {
        return new ListApplicationsService(applicationRepositoryPort);
    }

    @Bean
    public GetApplicationUseCase getApplicationUseCase(ApplicationRepositoryPort applicationRepositoryPort) {
        return new GetApplicationService(applicationRepositoryPort);
    }

    @Bean
    public StartDeploymentUseCase startDeploymentUseCase(
            ApplicationRepositoryPort applicationRepositoryPort,
            DeploymentRepositoryPort deploymentRepositoryPort,
            ContainerRepositoryPort containerRepositoryPort,
            PortAllocatorPort portAllocatorPort,
            DeploymentWorkspacePort deploymentWorkspacePort,
            GitClientPort gitClientPort,
            DockerRuntimePort dockerRuntimePort,
            DeploymentLogPublisherPort deploymentLogPublisherPort,
            DeploymentPolicy deploymentPolicy) {
        return new StartDeploymentService(
                applicationRepositoryPort,
                deploymentRepositoryPort,
                containerRepositoryPort,
                portAllocatorPort,
                deploymentWorkspacePort,
                gitClientPort,
                dockerRuntimePort,
                deploymentLogPublisherPort,
                deploymentPolicy);
    }

    @Bean
    public ListDeploymentsUseCase listDeploymentsUseCase(DeploymentRepositoryPort deploymentRepositoryPort) {
        return new ListDeploymentsService(deploymentRepositoryPort);
    }

    @Bean
    public GetDeploymentUseCase getDeploymentUseCase(DeploymentRepositoryPort deploymentRepositoryPort) {
        return new GetDeploymentService(deploymentRepositoryPort);
    }

    @Bean
    public GetDeploymentLogsUseCase getDeploymentLogsUseCase(
            DeploymentRepositoryPort deploymentRepositoryPort,
            DeploymentLogPort deploymentLogPort) {
        return new GetDeploymentLogsService(deploymentRepositoryPort, deploymentLogPort);
    }

    @Bean
    public ListContainersUseCase listContainersUseCase(ContainerRepositoryPort containerRepositoryPort) {
        return new ListContainersService(containerRepositoryPort);
    }

    @Bean
    public StopContainerUseCase stopContainerUseCase(DockerRuntimePort dockerRuntimePort) {
        return new StopContainerService(dockerRuntimePort);
    }
}
