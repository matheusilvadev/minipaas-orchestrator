package com.paas.application.port.out;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.paas.domain.model.ContainerInstance;

public interface ContainerRepositoryPort {
    ContainerInstance save(ContainerInstance containerInstance);

    List<ContainerInstance> findAll();

    Optional<ContainerInstance> findByDeploymentId(UUID deploymentId);

    void delete(UUID id);
}
