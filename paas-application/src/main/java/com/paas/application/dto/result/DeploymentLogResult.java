package com.paas.application.dto.result;

import java.util.List;
import java.util.UUID;

public record DeploymentLogResult(UUID deploymentId, List<String> lines) {

}
