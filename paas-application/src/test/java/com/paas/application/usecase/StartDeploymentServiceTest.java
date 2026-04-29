package com.paas.application.usecase;

import com.paas.application.dto.command.StartDeploymentCommand;
import com.paas.application.dto.result.DeploymentResult;
import com.paas.application.exception.ApplicationNotFoundException;
import com.paas.application.exception.DeploymentFailedException;
import com.paas.application.port.out.ApplicationRepositoryPort;
import com.paas.application.port.out.DeploymentRepositoryPort;
import com.paas.domain.model.Application;
import com.paas.domain.model.Deployment;
import com.paas.domain.service.DeploymentPolicy;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StartDeploymentServiceTest {

        @Mock
        private ApplicationRepositoryPort applicationRepositoryPort;

        @Mock
        private DeploymentRepositoryPort deploymentRepositoryPort;

        private StartDeploymentService startDeploymentService;

        @BeforeEach
        void setUp() {
                startDeploymentService = new StartDeploymentService(
                                applicationRepositoryPort,
                                deploymentRepositoryPort,
                                new DeploymentPolicy());
        }

        @Test
        void shouldStartDeploymentWhenApplicationExistsAndThereAreNoActiveDeployments() {
                UUID applicationId = UUID.randomUUID();
                UUID deploymentId = UUID.randomUUID();
                StartDeploymentCommand command = new StartDeploymentCommand(applicationId);
                Application application = new Application(
                                applicationId,
                                "my-service",
                                new RepositoryUrl("https://github.com/example/my-service.git"),
                                "main");

                when(applicationRepositoryPort.findById(applicationId)).thenReturn(Optional.of(application));
                when(deploymentRepositoryPort.findActiveByApplicationId(applicationId)).thenReturn(List.of());
                when(deploymentRepositoryPort.save(any(Deployment.class)))
                                .thenReturn(new Deployment(deploymentId, applicationId));

                DeploymentResult result = startDeploymentService.execute(command);

                ArgumentCaptor<Deployment> deploymentCaptor = ArgumentCaptor.forClass(Deployment.class);
                verify(deploymentRepositoryPort).save(deploymentCaptor.capture());

                Deployment deploymentToPersist = deploymentCaptor.getValue();

                assertThat(deploymentToPersist.applicationId()).isEqualTo(applicationId);
                assertThat(result.id()).isEqualTo(deploymentId);
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
}
