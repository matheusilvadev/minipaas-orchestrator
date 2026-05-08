package com.paas.application.port.out;

import java.util.UUID;

public interface DeploymentLogPublisherPort {
    void publish(UUID deploymentId, String message);
}
