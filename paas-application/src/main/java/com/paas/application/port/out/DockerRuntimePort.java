package com.paas.application.port.out;

public interface DockerRuntimePort {
    void stopContainer(String containerId);
}
