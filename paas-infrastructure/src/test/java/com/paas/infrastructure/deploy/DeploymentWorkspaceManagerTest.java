package com.paas.infrastructure.deploy;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.nio.file.Path;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class DeploymentWorkspaceManagerTest {

    @TempDir
    Path tempDir;

    @Test
    void shouldCreateAndCleanupWorkspace() {
        DeploymentWorkspaceManager manager = new DeploymentWorkspaceManager(tempDir.toString());
        UUID deploymentId = UUID.randomUUID();

        // Criar
        File workspace = manager.createWorkspace(deploymentId);
        assertTrue(workspace.exists());
        assertTrue(workspace.isDirectory());

        // Limpar
        manager.cleanup(deploymentId);
        assertFalse(workspace.exists());
    }
}
