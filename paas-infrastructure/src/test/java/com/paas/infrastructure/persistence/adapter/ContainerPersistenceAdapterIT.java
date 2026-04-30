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

import com.paas.domain.model.ContainerInstance;
import com.paas.domain.valueobject.PortNumber;

@DataJpaTest
@Import({ ContainerPersistenceAdapter.class })
@ActiveProfiles("test")
public class ContainerPersistenceAdapterIT {

    @Autowired
    private ContainerPersistenceAdapter adapter;

    @Test
    @DisplayName("Deve salvar e recuperar uma instância de container")
    void shouldSaveAndRetrieveContainer() {
        // Arrange
        UUID deploymentId = UUID.randomUUID();
        ContainerInstance container = new ContainerInstance(
                UUID.randomUUID(),
                deploymentId,
                "docker-container-123",
                new PortNumber(8080));

        // Act
        adapter.save(container);
        Optional<ContainerInstance> found = adapter.findByDeploymentId(deploymentId);

        // Assert
        assertTrue(found.isPresent());
        assertEquals("docker-container-123", found.get().containerId());
        assertEquals(8080, found.get().portNumber().value());
    }

}
