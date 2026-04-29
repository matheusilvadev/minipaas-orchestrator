package com.paas.application.usecase;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.paas.application.port.out.DockerRuntimePort;

@ExtendWith(MockitoExtension.class)
public class StopContainerServiceTest {
    @Mock
    private DockerRuntimePort dockerRuntimePort;

    private StopContainerService stopContainerService;

    @BeforeEach
    void setUp() {
        stopContainerService = new StopContainerService(dockerRuntimePort);
    }

    @Test
    void shouldCallRuntimeToStopContainer() {
        String containerId = "docker-123";

        stopContainerService.execute(containerId);
        verify(dockerRuntimePort, times(1)).stopContainer(containerId);
    }
}
