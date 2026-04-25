package com.paas.domain.model;

import com.paas.domain.valueobject.RepositoryUrl;

import java.util.UUID;

public record Application(
        UUID id,
        String name,
        RepositoryUrl repositoryUrl,
        String branchName) {
    public Application {
        if (id == null) {
            throw new IllegalArgumentException("Application ID is required");
        }

        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Application name is required");
        }

        if (repositoryUrl == null) {
            throw new IllegalArgumentException("Repository URL is required");
        }

        // Regra de conveniência: se não informar branch, assume 'main'
        if (branchName == null || branchName.isBlank()) {
            branchName = "main";
        }
    }
}
