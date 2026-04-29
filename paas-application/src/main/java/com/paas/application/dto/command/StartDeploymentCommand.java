package com.paas.application.dto.command;

import java.util.UUID;

public record StartDeploymentCommand(UUID applicationId) {

}
