package com.paas.application.usecase;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.paas.application.dto.result.DeploymentLogResult;
import com.paas.application.exception.DeploymentNotFoundException;
import com.paas.application.port.out.DeploymentLogPort;
import com.paas.application.port.out.DeploymentRepositoryPort;
import com.paas.domain.model.Deployment;

@ExtendWith(MockitoExtension.class)
public class GetDeploymentLogsServiceTest {

    @Mock
    private DeploymentRepositoryPort deploymentRepositoryPort;

    @Mock
    private DeploymentLogPort deploymentLogPort;

    private GetDeploymentLogsService getDeploymentLogsService;

    @BeforeEach
    void setUp() {
        getDeploymentLogsService = new GetDeploymentLogsService(deploymentRepositoryPort, deploymentLogPort);
    }

    @Test
    void shouldReturnLogsWhenDeploymentExists() {
        UUID deploymentId = UUID.randomUUID();
        Deployment deployment = new Deployment(deploymentId, UUID.randomUUID());
        List<String> expectedLogs = List.of("Step 1/3: Cloning...", "Step 2/3: Building...");

        when(deploymentRepositoryPort.findById(deploymentId)).thenReturn(Optional.of(deployment));
        when(deploymentLogPort.getLogs(deploymentId)).thenReturn(expectedLogs);

        DeploymentLogResult result = getDeploymentLogsService.execute(deploymentId);

        assertThat(result.deploymentId()).isEqualTo(deploymentId);
        assertThat(result.lines()).containsExactlyElementsOf(expectedLogs);
    }

    @Test
    void shouldThrowExceptionWhenDeploymentDoesNotExist() {
        UUID deploymentId = UUID.randomUUID();
        when(deploymentRepositoryPort.findById(deploymentId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> getDeploymentLogsService.execute(deploymentId))
                .isInstanceOf(DeploymentNotFoundException.class);

        verify(deploymentLogPort, never()).getLogs(any());
    }

}
