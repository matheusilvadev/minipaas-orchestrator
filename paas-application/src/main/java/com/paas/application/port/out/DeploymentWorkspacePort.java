package com.paas.application.port.out;

import java.io.File;
import java.util.UUID;

public interface DeploymentWorkspacePort {
    File createWorkspace(UUID deploymentId);

    void cleanup(UUID deploymentId);
}
