package com.paas.infrastructure.docker;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.paas.application.exception.ContainerNotFoundException;
import com.paas.application.port.out.ContainerRepositoryPort;
import com.paas.application.port.out.DeploymentLogPort;
import com.paas.domain.model.ContainerInstance;

@Component
public class DockerDeploymentLogAdapter implements DeploymentLogPort {

    private final ContainerRepositoryPort containerRepositoryPort;

    public DockerDeploymentLogAdapter(ContainerRepositoryPort containerRepositoryPort) {
        this.containerRepositoryPort = containerRepositoryPort;
    }

    @Override
    public List<String> getLogs(UUID deploymentId) {
        ContainerInstance container = containerRepositoryPort.findByDeploymentId(deploymentId)
                .orElseThrow(() -> new ContainerNotFoundException(
                        "Container not found for deployment: " + deploymentId));

        try {
            ProcessBuilder processBuilder = new ProcessBuilder(
                    "docker", "logs", "--tail", "200", container.containerId());
            Process process = processBuilder.start();

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                List<String> logs = reader.lines().toList();
                int exitCode = process.waitFor();

                if (exitCode != 0) {
                    throw new RuntimeException("Could not read Docker logs for container: " + container.containerId());
                }

                return logs;
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Docker log reading interrupted", e);
        } catch (Exception e) {
            throw new RuntimeException("Docker log reading failed", e);
        }
    }
}
