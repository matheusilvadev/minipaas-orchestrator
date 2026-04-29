package com.paas.application.usecase;

import com.paas.application.dto.result.DeploymentResult;
import com.paas.application.port.out.DeploymentRepositoryPort;
import com.paas.domain.model.Deployment;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ListDeploymentsServiceTest {

    @Mock
    private DeploymentRepositoryPort deploymentRepositoryPort;

    @InjectMocks
    private ListDeploymentsService listDeploymentsService;

    @Test
    void shouldListDeploymentsByApplicationId() {
        UUID applicationId = UUID.randomUUID();
        UUID firstDeploymentId = UUID.randomUUID();
        UUID secondDeploymentId = UUID.randomUUID();

        when(deploymentRepositoryPort.findByApplicationId(applicationId)).thenReturn(List.of(
                new Deployment(firstDeploymentId, applicationId),
                new Deployment(secondDeploymentId, applicationId)
        ));

        List<DeploymentResult> result = listDeploymentsService.execute(applicationId);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).id()).isEqualTo(firstDeploymentId);
        assertThat(result.get(1).id()).isEqualTo(secondDeploymentId);
    }
}
