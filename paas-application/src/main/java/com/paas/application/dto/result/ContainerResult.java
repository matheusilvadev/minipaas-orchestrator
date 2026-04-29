package com.paas.application.dto.result;

import java.util.UUID;

public record ContainerResult(UUID id, UUID deploymentId, String containerId, Integer port) {

}
