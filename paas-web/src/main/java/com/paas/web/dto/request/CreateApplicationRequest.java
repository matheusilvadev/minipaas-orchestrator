package com.paas.web.dto.request;

import jakarta.validation.constraints.NotBlank;

public record CreateApplicationRequest(
        @NotBlank(message = "name is required") String name,
        @NotBlank(message = "repositoryUrl is required") String repositoryUrl,
        String branchName) {
}
