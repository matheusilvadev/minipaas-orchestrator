package com.paas.application.usecase;

import com.paas.application.dto.result.DeploymentResult;
import com.paas.application.exception.DeploymentNotFoundException;
import com.paas.application.port.out.DeploymentRepositoryPort;
import com.paas.domain.model.Deployment;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetDeploymentServiceTest {

    @Mock
    private DeploymentRepositoryPort deploymentRepositoryPort;

    @InjectMocks
    private GetDeploymentService getDeploymentService;

    @Test
    void shouldReturnDeploymentWhenItExists() {
        UUID deploymentId = UUID.randomUUID();
        UUID applicationId = UUID.randomUUID();
        Deployment deployment = new Deployment(deploymentId, applicationId);
        deployment.startBuilding();

        when(deploymentRepositoryPort.findById(deploymentId)).thenReturn(Optional.of(deployment));

        DeploymentResult result = getDeploymentService.execute(deploymentId);

        assertThat(result.id()).isEqualTo(deploymentId);
        assertThat(result.status()).isEqualTo("BUILDING");
    }

    @Test
    void shouldThrowExceptionWhenDeploymentDoesNotExist() {
        UUID deploymentId = UUID.randomUUID();

        when(deploymentRepositoryPort.findById(deploymentId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> getDeploymentService.execute(deploymentId))
                .isInstanceOf(DeploymentNotFoundException.class);
    }
}
