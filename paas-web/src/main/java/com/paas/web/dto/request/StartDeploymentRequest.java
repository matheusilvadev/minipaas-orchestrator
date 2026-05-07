package com.paas.web.dto.request;

import java.util.UUID;

import jakarta.validation.constraints.NotNull;

public record StartDeploymentRequest(
        @NotNull(message = "applicationId is required") UUID applicationId) {
}
