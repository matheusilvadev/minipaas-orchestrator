package com.paas.infrastructure.git;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.paas.domain.valueobject.RepositoryUrl;

public class GitCliClientTest {

    @Test
    @DisplayName("Deve converter VO e chamar o client com sucesso")
    void shouldCloneSuccessfully() {
        // Arrange
        GitCliClient cliMock = mock(GitCliClient.class);
        GitClientAdapter adapter = new GitClientAdapter(cliMock);

        RepositoryUrl url = new RepositoryUrl("https://github.com/bjornlbk/minipaas.git");
        File dest = new File("/tmp/test-clone");

        when(cliMock.executeClone(anyString(), anyString(), any(File.class))).thenReturn(dest);

        // Act
        File result = adapter.clone(url, "main", dest);

        // Assert
        assertEquals(dest, result);
        verify(cliMock).executeClone("https://github.com/bjornlbk/minipaas.git", "main", dest);
    }

}
