package com.paas.infrastructure.persistence.adapter;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.paas.application.port.out.DeploymentRepositoryPort;
import com.paas.domain.model.Deployment;
import com.paas.domain.model.DeploymentStatus;
import com.paas.infrastructure.persistence.entity.DeploymentEntity;
import com.paas.infrastructure.persistence.repository.SpringDataDeploymentJpaRepository;

@Component
public class DeploymentPersistenceAdapter implements DeploymentRepositoryPort {

    private final SpringDataDeploymentJpaRepository repository;

    public DeploymentPersistenceAdapter(SpringDataDeploymentJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<Deployment> findActiveByApplicationId(UUID applicationId) {
        return repository.findByApplicationIdAndStatusIn(
                applicationId,
                List.of(DeploymentStatus.PENDING, DeploymentStatus.BUILDING, DeploymentStatus.RUNNING)).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Deployment> findByApplicationId(UUID applicationId) {
        return repository.findByApplicationIdAndStatusIn(
                applicationId,
                List.of(DeploymentStatus.PENDING, DeploymentStatus.BUILDING, DeploymentStatus.RUNNING)).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Deployment> findById(UUID id) {
        return repository.findById(id)
                .map(this::toDomain);
    }

    @Override
    public Deployment save(Deployment deployment) {
        DeploymentEntity entity = new DeploymentEntity(
                deployment.id(),
                deployment.applicationId(),
                deployment.status());
        DeploymentEntity saved = repository.save(entity);
        return toDomain(saved);
    }

    private Deployment toDomain(DeploymentEntity entity) {
        return Deployment.rehydrate(
                entity.getId(),
                entity.getApplicationId(),
                entity.getStatus());
    }

}
