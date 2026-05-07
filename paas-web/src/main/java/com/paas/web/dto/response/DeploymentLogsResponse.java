package com.paas.web.dto.response;

import java.util.List;
import java.util.UUID;

public record DeploymentLogsResponse(UUID deploymentId, List<String> lines) {
}
