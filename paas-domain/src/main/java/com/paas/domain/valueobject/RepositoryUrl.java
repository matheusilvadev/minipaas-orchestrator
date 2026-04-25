package com.paas.domain.valueobject;

import com.paas.domain.exception.InvalidRepositoryException;

public record RepositoryUrl(String value) {
    public RepositoryUrl {
        if (value == null || value.isBlank()) {
            throw new InvalidRepositoryException("Repository URL cannot be empty");
        }

        if (!value.startsWith("https://github.com/")) {
            throw new InvalidRepositoryException("Only GitHub HTTPS repositories are supported");
        }
    }

}
