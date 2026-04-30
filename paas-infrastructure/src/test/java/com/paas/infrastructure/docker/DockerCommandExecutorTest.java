package com.paas.infrastructure.docker;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.File;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class DockerCommandExecutorTest {
    private final DockerCommandExecutor executor = new DockerCommandExecutor();

    @TempDir
    File tempDir;

    @Test
    @DisplayName("Deve gerar erro ao tentar buildar com Dockerfile inexistente")
    void shouldFailWhenDockerfileIsMissing() {
        // Arrange
        File emptyDir = new File(tempDir, "empty-" + UUID.randomUUID());
        emptyDir.mkdirs();

        // Act & Assert
        // O método 'build' tentará executar 'docker build' e o Docker retornará erro
        // (exit value != 0)
        assertThrows(RuntimeException.class, () -> executor.build("test-app", emptyDir));
    }
}
