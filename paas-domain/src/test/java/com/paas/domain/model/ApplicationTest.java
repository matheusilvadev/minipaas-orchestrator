package com.paas.domain.model;

import com.paas.domain.valueobject.RepositoryUrl;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ApplicationTest {

    @Test
    void shouldCreateApplicationWithValidStructure() {
        UUID id = UUID.randomUUID();
        RepositoryUrl repositoryUrl = new RepositoryUrl("https://github.com/example/my-service");
        String branch = "develop";

        Application application = new Application(
                id,
                "my-service",
                repositoryUrl,
                branch);

        assertThat(application.id()).isEqualTo(id);
        assertThat(application.name()).isEqualTo("my-service");
        assertThat(application.repositoryUrl()).isEqualTo(repositoryUrl);
        assertThat(application.branchName()).isEqualTo("develop");
    }

    @Test
    void shouldAssignDefaultBranchWhenBranchIsNull() {
        // Cenário: Branch enviada como null
        Application application = new Application(
                UUID.randomUUID(),
                "my-service",
                new RepositoryUrl("https://github.com/example/repo"),
                null);

        // Validação: O Record deve garantir a atribuição de "main"
        assertThat(application.branchName()).isEqualTo("main");
    }

    @Test
    void shouldAssignDefaultBranchWhenBranchIsBlank() {
        // Cenário: Branch enviada como string vazia
        Application application = new Application(
                UUID.randomUUID(),
                "my-service",
                new RepositoryUrl("https://github.com/example/repo"),
                "  ");

        assertThat(application.branchName()).isEqualTo("main");
    }
}
