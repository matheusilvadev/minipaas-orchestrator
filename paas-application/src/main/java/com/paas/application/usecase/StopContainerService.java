package com.paas.application.usecase;

import com.paas.application.port.in.StopContainerUseCase;
import com.paas.application.port.out.DockerRuntimePort;

public class StopContainerService implements StopContainerUseCase {

    private final DockerRuntimePort dockerRuntimePort;

    public StopContainerService(DockerRuntimePort dockerRuntimePort) {
        this.dockerRuntimePort = dockerRuntimePort;
    }

    @Override
    public void execute(String containerId) {
        dockerRuntimePort.stopContainer(containerId);
    }
}
