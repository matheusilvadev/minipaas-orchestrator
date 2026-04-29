package com.paas.application.port.out;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.paas.domain.model.Deployment;

public interface DeploymentRepositoryPort {
    Deployment save(Deployment deployment);

    List<Deployment> findActiveByApplicationId(UUID applicationId);

    List<Deployment> findByApplicationId(UUID applicationId);

    Optional<Deployment> findById(UUID id);
}
