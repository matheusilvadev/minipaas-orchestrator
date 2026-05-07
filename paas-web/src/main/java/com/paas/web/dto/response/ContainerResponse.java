package com.paas.web.dto.response;

import java.util.UUID;

public record ContainerResponse(UUID id, UUID deploymentId, String containerId, Integer port) {
}
