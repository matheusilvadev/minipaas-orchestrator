package com.paas.infrastructure.persistence.entity;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "container_instances")
public class ContainerInstanceEntity {

    @Id
    private UUID id;

    @Column(nullable = false)
    private UUID deploymentId;

    @Column(nullable = false)
    private String containerId;

    @Column(nullable = false)
    private Integer port;

    public ContainerInstanceEntity() {
    }

    public ContainerInstanceEntity(UUID id, UUID deploymentId, String containerId, Integer port) {
        this.id = id;
        this.deploymentId = deploymentId;
        this.containerId = containerId;
        this.port = port;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getDeploymentId() {
        return deploymentId;
    }

    public void setDeploymentId(UUID deploymentId) {
        this.deploymentId = deploymentId;
    }

    public String getContainerId() {
        return containerId;
    }

    public void setContainerId(String containerId) {
        this.containerId = containerId;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

}
