package com.paas.application.usecase;

import java.util.List;
import java.util.stream.Collectors;

import com.paas.application.dto.result.ContainerResult;
import com.paas.application.port.in.ListContainersUseCase;
import com.paas.application.port.out.ContainerRepositoryPort;

public class ListContainersService implements ListContainersUseCase {

    private final ContainerRepositoryPort containerRepositoryPort;

    public ListContainersService(ContainerRepositoryPort containerRepositoryPort) {
        this.containerRepositoryPort = containerRepositoryPort;
    }

    @Override
    public List<ContainerResult> execute() {
        return containerRepositoryPort.findAll().stream()
                .map(c -> new ContainerResult(
                        c.id(),
                        c.deploymentId(),
                        c.containerId(),
                        c.portNumber().value()))
                .collect(Collectors.toList());
    }

}
