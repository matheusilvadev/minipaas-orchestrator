package com.paas.domain.model;

import com.paas.domain.valueobject.PortNumber;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ContainerInstanceTest {

    @Test
    void shouldCreateContainerInstanceWithValidStructure() {
        UUID id = UUID.randomUUID();
        UUID deploymentId = UUID.randomUUID();
        PortNumber portNumber = new PortNumber(8080);

        ContainerInstance containerInstance = new ContainerInstance(
                id,
                deploymentId,
                "container-123",
                portNumber);

        assertThat(containerInstance.id()).isEqualTo(id);
        assertThat(containerInstance.deploymentId()).isEqualTo(deploymentId);
        assertThat(containerInstance.containerId()).isEqualTo("container-123");
        assertThat(containerInstance.portNumber()).isEqualTo(portNumber);
    }
}
