package com.paas.web.mapper;

import org.springframework.stereotype.Component;

import com.paas.application.dto.result.ContainerResult;
import com.paas.web.dto.response.ContainerResponse;

@Component
public class ContainerHttpMapper {

    public ContainerResponse toResponse(ContainerResult result) {
        return new ContainerResponse(result.id(), result.deploymentId(), result.containerId(), result.port());
    }
}
