package com.paas.application.usecase;

import com.paas.application.dto.command.StartDeploymentCommand;
import com.paas.application.dto.result.DeploymentResult;
import com.paas.application.exception.ApplicationNotFoundException;
import com.paas.application.exception.DeploymentFailedException;
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
import com.paas.domain.model.DeploymentStatus;
import com.paas.domain.service.DeploymentPolicy;
import com.paas.domain.valueobject.PortNumber;
import com.paas.domain.valueobject.RepositoryUrl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.io.File;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StartDeploymentServiceTest {

        @Mock
        private ApplicationRepositoryPort applicationRepositoryPort;

        @Mock
        private DeploymentRepositoryPort deploymentRepositoryPort;

        @Mock
        private ContainerRepositoryPort containerRepositoryPort;

        @Mock
        private PortAllocatorPort portAllocatorPort;

        @Mock
        private DeploymentWorkspacePort deploymentWorkspacePort;

        @Mock
        private GitClientPort gitClientPort;

        @Mock
        private DockerRuntimePort dockerRuntimePort;

        private StartDeploymentService startDeploymentService;

        @BeforeEach
        void setUp() {
                startDeploymentService = new StartDeploymentService(
                                applicationRepositoryPort,
                                deploymentRepositoryPort,
                                containerRepositoryPort,
                                portAllocatorPort,
                                deploymentWorkspacePort,
                                gitClientPort,
                                dockerRuntimePort,
                                new DeploymentPolicy());
        }

        @Test
        void shouldCompleteDeploymentFlowWhenApplicationExistsAndThereAreNoActiveDeployments() {
                UUID applicationId = UUID.randomUUID();
                UUID deploymentId = UUID.randomUUID();
                StartDeploymentCommand command = new StartDeploymentCommand(applicationId);
                Application application = new Application(
                                applicationId,
                                "my-service",
                                new RepositoryUrl("https://github.com/example/my-service.git"),
                                "main");
                PortNumber allocatedPort = new PortNumber(8081);
                File workspace = new File("build/workspaces/" + deploymentId);

                when(applicationRepositoryPort.findById(applicationId)).thenReturn(Optional.of(application));
                when(deploymentRepositoryPort.findActiveByApplicationId(applicationId)).thenReturn(List.of());
                when(deploymentRepositoryPort.save(any(Deployment.class)))
                                .thenAnswer(invocation -> {
                                        Deployment deployment = invocation.getArgument(0);
                                        if (deployment.status() == DeploymentStatus.PENDING) {
                                                return Deployment.rehydrate(deploymentId, applicationId,
                                                                DeploymentStatus.PENDING);
                                        }
                                        return Deployment.rehydrate(deploymentId, applicationId, deployment.status());
                                });
                when(portAllocatorPort.allocate()).thenReturn(allocatedPort);
                when(deploymentWorkspacePort.createWorkspace(deploymentId)).thenReturn(workspace);
                when(dockerRuntimePort.runContainer(anyString(), anyInt())).thenReturn("container-123");

                DeploymentResult result = startDeploymentService.execute(command);

                ArgumentCaptor<Deployment> deploymentCaptor = ArgumentCaptor.forClass(Deployment.class);
                verify(deploymentRepositoryPort, times(3)).save(deploymentCaptor.capture());
                List<Deployment> savedDeployments = deploymentCaptor.getAllValues();
                assertThat(savedDeployments)
                                .extracting(Deployment::status)
                                .containsExactly(
                                                DeploymentStatus.PENDING,
                                                DeploymentStatus.BUILDING,
                                                DeploymentStatus.RUNNING);

                ArgumentCaptor<ContainerInstance> containerCaptor = ArgumentCaptor.forClass(ContainerInstance.class);
                verify(containerRepositoryPort).save(containerCaptor.capture());
                ContainerInstance savedContainer = containerCaptor.getValue();

                verify(portAllocatorPort).allocate();
                verify(deploymentWorkspacePort).createWorkspace(deploymentId);
                verify(gitClientPort).clone(application.repositoryUrl(), application.branchName(), workspace);
                verify(dockerRuntimePort).buildImage("my-service:" + deploymentId, workspace);
                verify(dockerRuntimePort).runContainer("my-service:" + deploymentId, allocatedPort.value());
                verify(deploymentWorkspacePort, never()).cleanup(any(UUID.class));
                verify(portAllocatorPort, never()).release(any(PortNumber.class));

                assertThat(savedContainer.deploymentId()).isEqualTo(deploymentId);
                assertThat(savedContainer.containerId()).isEqualTo("container-123");
                assertThat(savedContainer.portNumber()).isEqualTo(allocatedPort);
                assertThat(result.id()).isEqualTo(deploymentId);
                assertThat(result.status()).isEqualTo(DeploymentStatus.RUNNING.name());
        }

        @Test
        void shouldRejectDeploymentWhenPolicyDeniesBecauseThereIsAlreadyOneInProgress() {
                UUID applicationId = UUID.randomUUID();
                StartDeploymentCommand command = new StartDeploymentCommand(applicationId);
                Application application = new Application(
                                applicationId,
                                "my-service",
                                new RepositoryUrl("https://github.com/example/my-service.git"),
                                "main");
                Deployment activeDeployment = new Deployment(UUID.randomUUID(), applicationId);

                when(applicationRepositoryPort.findById(applicationId)).thenReturn(Optional.of(application));
                when(deploymentRepositoryPort.findActiveByApplicationId(applicationId))
                                .thenReturn(List.of(activeDeployment));

                assertThatThrownBy(() -> startDeploymentService.execute(command))
                                .isInstanceOf(DeploymentFailedException.class);

                verify(deploymentRepositoryPort, never()).save(any(Deployment.class));
                verify(portAllocatorPort, never()).allocate();
        }

        @Test
        void shouldRejectDeploymentWhenApplicationDoesNotExist() {
                UUID applicationId = UUID.randomUUID();
                StartDeploymentCommand command = new StartDeploymentCommand(applicationId);

                when(applicationRepositoryPort.findById(applicationId)).thenReturn(Optional.empty());

                assertThatThrownBy(() -> startDeploymentService.execute(command))
                                .isInstanceOf(ApplicationNotFoundException.class);

                verify(deploymentRepositoryPort, never()).findActiveByApplicationId(any(UUID.class));
                verify(deploymentRepositoryPort, never()).save(any(Deployment.class));
        }

        @Test
        void shouldMarkDeploymentAsFailedAndCleanupResourcesWhenGitCloneFails() {
                UUID applicationId = UUID.randomUUID();
                UUID deploymentId = UUID.randomUUID();
                StartDeploymentCommand command = new StartDeploymentCommand(applicationId);
                Application application = new Application(
                                applicationId,
                                "my-service",
                                new RepositoryUrl("https://github.com/example/my-service.git"),
                                "main");
                PortNumber allocatedPort = new PortNumber(8081);
                File workspace = new File("build/workspaces/" + deploymentId);

                when(applicationRepositoryPort.findById(applicationId)).thenReturn(Optional.of(application));
                when(deploymentRepositoryPort.findActiveByApplicationId(applicationId)).thenReturn(List.of());
                when(deploymentRepositoryPort.save(any(Deployment.class)))
                                .thenAnswer(invocation -> {
                                        Deployment deployment = invocation.getArgument(0);
                                        return Deployment.rehydrate(deploymentId, applicationId, deployment.status());
                                });
                when(portAllocatorPort.allocate()).thenReturn(allocatedPort);
                when(deploymentWorkspacePort.createWorkspace(deploymentId)).thenReturn(workspace);
                when(gitClientPort.clone(application.repositoryUrl(), application.branchName(), workspace))
                                .thenThrow(new RuntimeException("git clone failed"));

                assertThatThrownBy(() -> startDeploymentService.execute(command))
                                .isInstanceOf(DeploymentFailedException.class)
                                .hasMessageContaining("git clone failed");

                ArgumentCaptor<Deployment> deploymentCaptor = ArgumentCaptor.forClass(Deployment.class);
                verify(deploymentRepositoryPort, times(2)).save(deploymentCaptor.capture());
                assertThat(deploymentCaptor.getAllValues())
                                .extracting(Deployment::status)
                                .containsExactly(DeploymentStatus.PENDING, DeploymentStatus.FAILED);

                verify(portAllocatorPort).release(allocatedPort);
                verify(deploymentWorkspacePort).cleanup(deploymentId);
                verify(dockerRuntimePort, never()).buildImage(anyString(), any(File.class));
                verify(dockerRuntimePort, never()).runContainer(anyString(), anyInt());
                verify(containerRepositoryPort, never()).save(any(ContainerInstance.class));
        }

        @Test
        void shouldMarkDeploymentAsFailedAndCleanupResourcesWhenDockerBuildFails() {
                UUID applicationId = UUID.randomUUID();
                UUID deploymentId = UUID.randomUUID();
                StartDeploymentCommand command = new StartDeploymentCommand(applicationId);
                Application application = new Application(
                                applicationId,
                                "my-service",
                                new RepositoryUrl("https://github.com/example/my-service.git"),
                                "main");
                PortNumber allocatedPort = new PortNumber(8081);
                File workspace = new File("build/workspaces/" + deploymentId);

                when(applicationRepositoryPort.findById(applicationId)).thenReturn(Optional.of(application));
                when(deploymentRepositoryPort.findActiveByApplicationId(applicationId)).thenReturn(List.of());
                when(deploymentRepositoryPort.save(any(Deployment.class)))
                                .thenAnswer(invocation -> {
                                        Deployment deployment = invocation.getArgument(0);
                                        return Deployment.rehydrate(deploymentId, applicationId, deployment.status());
                                });
                when(portAllocatorPort.allocate()).thenReturn(allocatedPort);
                when(deploymentWorkspacePort.createWorkspace(deploymentId)).thenReturn(workspace);
                org.mockito.Mockito.doThrow(new RuntimeException("docker build failed"))
                                .when(dockerRuntimePort).buildImage("my-service:" + deploymentId, workspace);

                assertThatThrownBy(() -> startDeploymentService.execute(command))
                                .isInstanceOf(DeploymentFailedException.class)
                                .hasMessageContaining("docker build failed");

                ArgumentCaptor<Deployment> deploymentCaptor = ArgumentCaptor.forClass(Deployment.class);
                verify(deploymentRepositoryPort, times(3)).save(deploymentCaptor.capture());
                assertThat(deploymentCaptor.getAllValues())
                                .extracting(Deployment::status)
                                .containsExactly(
                                                DeploymentStatus.PENDING,
                                                DeploymentStatus.BUILDING,
                                                DeploymentStatus.FAILED);

                verify(portAllocatorPort).release(allocatedPort);
                verify(deploymentWorkspacePort).cleanup(deploymentId);
                verify(dockerRuntimePort, never()).runContainer(anyString(), anyInt());
                verify(containerRepositoryPort, never()).save(any(ContainerInstance.class));
        }

        @Test
        void shouldMarkDeploymentAsFailedAndCleanupResourcesWhenDockerRunFails() {
                UUID applicationId = UUID.randomUUID();
                UUID deploymentId = UUID.randomUUID();
                StartDeploymentCommand command = new StartDeploymentCommand(applicationId);
                Application application = new Application(
                                applicationId,
                                "my-service",
                                new RepositoryUrl("https://github.com/example/my-service.git"),
                                "main");
                PortNumber allocatedPort = new PortNumber(8081);
                File workspace = new File("build/workspaces/" + deploymentId);

                when(applicationRepositoryPort.findById(applicationId)).thenReturn(Optional.of(application));
                when(deploymentRepositoryPort.findActiveByApplicationId(applicationId)).thenReturn(List.of());
                when(deploymentRepositoryPort.save(any(Deployment.class)))
                                .thenAnswer(invocation -> {
                                        Deployment deployment = invocation.getArgument(0);
                                        return Deployment.rehydrate(deploymentId, applicationId, deployment.status());
                                });
                when(portAllocatorPort.allocate()).thenReturn(allocatedPort);
                when(deploymentWorkspacePort.createWorkspace(deploymentId)).thenReturn(workspace);
                when(dockerRuntimePort.runContainer(anyString(), anyInt()))
                                .thenThrow(new RuntimeException("docker run failed"));

                assertThatThrownBy(() -> startDeploymentService.execute(command))
                                .isInstanceOf(DeploymentFailedException.class)
                                .hasMessageContaining("docker run failed");

                ArgumentCaptor<Deployment> deploymentCaptor = ArgumentCaptor.forClass(Deployment.class);
                verify(deploymentRepositoryPort, times(3)).save(deploymentCaptor.capture());
                assertThat(deploymentCaptor.getAllValues())
                                .extracting(Deployment::status)
                                .containsExactly(
                                                DeploymentStatus.PENDING,
                                                DeploymentStatus.BUILDING,
                                                DeploymentStatus.FAILED);

                verify(portAllocatorPort).release(allocatedPort);
                verify(deploymentWorkspacePort).cleanup(deploymentId);
                verify(dockerRuntimePort, never()).stopContainer(anyString());
                verify(containerRepositoryPort, never()).save(any(ContainerInstance.class));
        }
}
