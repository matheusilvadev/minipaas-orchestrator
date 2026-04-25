package com.paas.domain.service;

import com.paas.domain.model.Deployment;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class DeploymentPolicyTest {

    @Test
    void shouldAllowDeploymentWhenApplicationHasNoActiveDeployment() {
        UUID applicationId = UUID.randomUUID();
        UUID anotherApplicationId = UUID.randomUUID();

        Deployment finishedDeployment = new Deployment(UUID.randomUUID(), anotherApplicationId);
        finishedDeployment.startBuilding();
        finishedDeployment.markRunning();

        DeploymentPolicy policy = new DeploymentPolicy();

        boolean canStartDeployment = policy.canStartDeployment(
                applicationId,
                List.of(finishedDeployment)
        );

        assertThat(canStartDeployment).isTrue();
    }

    @Test
    void shouldRejectDeploymentWhenApplicationAlreadyHasPendingDeployment() {
        UUID applicationId = UUID.randomUUID();
        Deployment pendingDeployment = new Deployment(UUID.randomUUID(), applicationId);

        DeploymentPolicy policy = new DeploymentPolicy();

        boolean canStartDeployment = policy.canStartDeployment(
                applicationId,
                List.of(pendingDeployment)
        );

        assertThat(canStartDeployment).isFalse();
    }

    @Test
    void shouldRejectDeploymentWhenApplicationAlreadyHasBuildingDeployment() {
        UUID applicationId = UUID.randomUUID();
        Deployment buildingDeployment = new Deployment(UUID.randomUUID(), applicationId);
        buildingDeployment.startBuilding();

        DeploymentPolicy policy = new DeploymentPolicy();

        boolean canStartDeployment = policy.canStartDeployment(
                applicationId,
                List.of(buildingDeployment)
        );

        assertThat(canStartDeployment).isFalse();
    }
}
