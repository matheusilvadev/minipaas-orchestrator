package com.paas.domain.service;

import java.util.List;
import java.util.UUID;

import com.paas.domain.model.Deployment;
import com.paas.domain.model.DeploymentStatus;

public class DeploymentPolicy {
    public boolean canStartDeployment(UUID applicationId, List<Deployment> activeDeployments) {
        return activeDeployments.stream()
                .filter(d -> d.applicationId().equals(applicationId))
                .noneMatch(d -> d.status() == DeploymentStatus.PENDING ||
                        d.status() == DeploymentStatus.BUILDING);
    }
}
