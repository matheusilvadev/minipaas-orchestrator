package com.paas.application.port.out;

import java.util.List;
import java.util.UUID;

public interface DeploymentLogPort {
    List<String> getLogs(UUID deploymentId);
}
