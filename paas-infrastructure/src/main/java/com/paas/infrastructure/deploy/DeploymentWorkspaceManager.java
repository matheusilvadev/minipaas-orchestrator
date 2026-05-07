package com.paas.infrastructure.deploy;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.FileSystemUtils;

import com.paas.application.port.out.DeploymentWorkspacePort;

@Component
public class DeploymentWorkspaceManager implements DeploymentWorkspacePort {
    private final String baseWorkDir;

    // O valor virá do application.properties, ex:
    // paas.deploy.base-dir=/home/user/paas/workspaces
    public DeploymentWorkspaceManager(@Value("${paas.deploy.base-dir:/tmp/paas/workspaces}") String baseWorkDir) {
        this.baseWorkDir = baseWorkDir;
    }

    @Override
    public File createWorkspace(UUID deploymentId) {
        Path path = Paths.get(baseWorkDir, deploymentId.toString());
        File directory = path.toFile();

        if (!directory.exists()) {
            boolean created = directory.mkdirs();
            if (!created) {
                throw new RuntimeException("The workspace directory could not be created: " + path);
            }
        }
        return directory;
    }

    @Override
    public void cleanup(UUID deploymentId) {
        Path path = Paths.get(baseWorkDir, deploymentId.toString());
        FileSystemUtils.deleteRecursively(path.toFile());
    }
}
