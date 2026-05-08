package com.paas.application.port.out;

import java.io.File;
import java.util.function.Consumer;

public interface DockerRuntimePort {
    void buildImage(String imageName, File contextDir);

    default void buildImage(String imageName, File contextDir, Consumer<String> logConsumer) {
        buildImage(imageName, contextDir);
    }

    String runContainer(String imageName, int port);

    void stopContainer(String containerId);
}
