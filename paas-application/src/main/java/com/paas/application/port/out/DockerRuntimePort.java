package com.paas.application.port.out;

import java.io.File;

public interface DockerRuntimePort {
    void buildImage(String imageName, File contextDir);

    String runContainer(String imageName, int port);

    void stopContainer(String containerId);
}
