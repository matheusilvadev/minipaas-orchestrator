package com.paas.infrastructure.persistence.adapter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import com.paas.domain.model.Deployment;
import com.paas.domain.model.DeploymentStatus;

@DataJpaTest
@Import({ DeploymentPersistenceAdapter.class })
@ActiveProfiles("test")
public class DeploymentPersistenceAdapterIT {

    @Autowired
    private DeploymentPersistenceAdapter adapter;

    @Test
    @DisplayName("Deve salvar e recuperar um deploy com status BUILDING")
    void shouldSaveAndRootDeployment() {

        // Arrange
        UUID appId = UUID.randomUUID();
        UUID deploymentId = UUID.randomUUID();

        Deployment deployment = new Deployment(deploymentId, appId);

        deployment.startBuilding();

        // Act
        adapter.save(deployment);
        Optional<Deployment> found = adapter.findById(deploymentId);

        // Assert
        assertTrue(found.isPresent());

        assertEquals(DeploymentStatus.BUILDING, found.get().status());
        assertEquals(appId, found.get().applicationId());
        assertEquals(deploymentId, found.get().id());
    }

}
