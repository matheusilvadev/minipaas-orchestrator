package com.paas.infrastructure.persistence.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.paas.infrastructure.persistence.entity.ContainerInstanceEntity;

@Repository
public interface SpringDataContainerJpaRepository extends JpaRepository<ContainerInstanceEntity, UUID> {
    Optional<ContainerInstanceEntity> findByDeploymentId(UUID deploymentId);
}
