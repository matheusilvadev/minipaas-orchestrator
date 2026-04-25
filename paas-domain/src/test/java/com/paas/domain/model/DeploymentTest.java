package com.paas.domain.model;

import com.paas.domain.exception.InvalidDeploymentStateException;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DeploymentTest {

    @Test
    void shouldCreateDeploymentWithPendingStatus() {
        Deployment deployment = new Deployment(
                UUID.randomUUID(),
                UUID.randomUUID());

        assertThat(deployment.id()).isNotNull();
        assertThat(deployment.applicationId()).isNotNull();
        assertThat(deployment.status()).isEqualTo(DeploymentStatus.PENDING);
    }

    @Test
    void shouldTransitionFromPendingToBuilding() {
        Deployment deployment = new Deployment(
                UUID.randomUUID(),
                UUID.randomUUID());

        deployment.startBuilding();

        assertThat(deployment.status()).isEqualTo(DeploymentStatus.BUILDING);
    }

    @Test
    void shouldTransitionFromBuildingToRunning() {
        Deployment deployment = new Deployment(
                UUID.randomUUID(),
                UUID.randomUUID());

        deployment.startBuilding();
        deployment.markRunning();

        assertThat(deployment.status()).isEqualTo(DeploymentStatus.RUNNING);
    }

    @Test
    void shouldTransitionToFailedWhenErrorOccurs() {
        Deployment deployment = new Deployment(
                UUID.randomUUID(),
                UUID.randomUUID());

        deployment.startBuilding();
        deployment.markFailed();

        assertThat(deployment.status()).isEqualTo(DeploymentStatus.FAILED);
    }

    @Test
    void shouldRejectInvalidTransitionFromPendingToRunning() {
        Deployment deployment = new Deployment(
                UUID.randomUUID(),
                UUID.randomUUID());

        assertThatThrownBy(deployment::markRunning)
                .isInstanceOf(InvalidDeploymentStateException.class);
    }
}
