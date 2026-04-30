package com.paas.infrastructure.persistence.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.paas.domain.model.DeploymentStatus;
import com.paas.infrastructure.persistence.entity.DeploymentEntity;

@Repository
public interface SpringDataDeploymentJpaRepository extends JpaRepository<DeploymentEntity, UUID> {
    List<DeploymentEntity> findByApplicationId(UUID applicationId);

    List<DeploymentEntity> findByApplicationIdAndStatusIn(UUID applicationId, List<DeploymentStatus> statuses);
}
