package com.paas.domain.model;

import java.util.UUID;

import com.paas.domain.valueobject.PortNumber;

public record ContainerInstance(
        UUID id,
        UUID deploymentId, // liga a instância ao evento que a criou
        String containerId,
        PortNumber portNumber) {
    public ContainerInstance {
        if (id == null || deploymentId == null)
            throw new IllegalArgumentException("IDs are required");
        if (containerId == null || containerId.isBlank())
            throw new IllegalArgumentException("Container ID is required");
        if (portNumber == null)
            throw new IllegalArgumentException("Port is required");
    }
}
