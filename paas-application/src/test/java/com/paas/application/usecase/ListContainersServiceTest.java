package com.paas.application.usecase;

import static org.mockito.Mockito.when;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;

import com.paas.application.dto.result.ContainerResult;
import com.paas.application.port.out.ContainerRepositoryPort;
import com.paas.domain.model.ContainerInstance;
import com.paas.domain.valueobject.PortNumber;

@ExtendWith(MockitoExtension.class)
public class ListContainersServiceTest {
    @Mock
    private ContainerRepositoryPort containerRepositoryPort;

    private ListContainersService listContainersService;

    @BeforeEach
    void setUp() {
        listContainersService = new ListContainersService(containerRepositoryPort);
    }

    @Test
    void shouldReturnAllContainers() {
        UUID id = UUID.randomUUID();
        UUID depId = UUID.randomUUID();

        ContainerInstance instance = new ContainerInstance(
                id,
                depId,
                "docker-sha-256",
                new PortNumber(8080));

        when(containerRepositoryPort.findAll()).thenReturn(List.of(instance));

        List<ContainerResult> result = listContainersService.execute();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).containerId()).isEqualTo("docker-sha-256");
        assertThat(result.get(0).port()).isEqualTo(8080);
    }
}
