package com.paas.infrastructure.docker;

import java.io.File;

import org.springframework.stereotype.Component;

import com.paas.application.port.out.DockerRuntimePort;

@Component
public class ProcessBuilderDockerRuntimeAdapter implements DockerRuntimePort {

    private final DockerCommandExecutor dockerExecutor;

    public ProcessBuilderDockerRuntimeAdapter(DockerCommandExecutor dockerExecutor) {
        this.dockerExecutor = dockerExecutor;
    }

    @Override
    public void buildImage(String imageName, File contextDir) {
        dockerExecutor.build(imageName, contextDir);

    }

    @Override
    public String runContainer(String imageName, int port) {
        return dockerExecutor.run(imageName, port);
    }

    @Override
    public void stopContainer(String containerId) {
        dockerExecutor.stop(containerId);
    }
}
