package com.paas.infrastructure.persistence.adapter;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.paas.application.port.out.ContainerRepositoryPort;
import com.paas.domain.model.ContainerInstance;
import com.paas.domain.valueobject.PortNumber;
import com.paas.infrastructure.persistence.entity.ContainerInstanceEntity;
import com.paas.infrastructure.persistence.repository.SpringDataContainerJpaRepository;

@Component
public class ContainerPersistenceAdapter implements ContainerRepositoryPort {

    private final SpringDataContainerJpaRepository repository;

    public ContainerPersistenceAdapter(SpringDataContainerJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public void delete(UUID id) {
        repository.deleteById(id);
    }

    @Override
    public List<ContainerInstance> findAll() {
        return repository.findAll().stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public Optional<ContainerInstance> findByDeploymentId(UUID deploymentId) {
        return repository.findByDeploymentId(deploymentId)
                .map(this::toDomain);
    }

    @Override
    public ContainerInstance save(ContainerInstance containerInstance) {
        ContainerInstanceEntity entity = new ContainerInstanceEntity(
                containerInstance.id(),
                containerInstance.deploymentId(),
                containerInstance.containerId(),
                containerInstance.portNumber().value());

        ContainerInstanceEntity saved = repository.save(entity);

        return toDomain(saved);
    }

    private ContainerInstance toDomain(ContainerInstanceEntity entity) {
        return new ContainerInstance(
                entity.getId(),
                entity.getDeploymentId(),
                entity.getContainerId(),
                new PortNumber(entity.getPort()));
    }

}
