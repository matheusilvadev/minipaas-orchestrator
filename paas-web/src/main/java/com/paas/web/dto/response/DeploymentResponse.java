package com.paas.web.dto.response;

import java.util.UUID;

public record DeploymentResponse(UUID id, String status) {
}
