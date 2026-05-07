package com.paas.web.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.paas.application.port.in.ListContainersUseCase;
import com.paas.application.port.in.StopContainerUseCase;
import com.paas.web.dto.response.ContainerResponse;
import com.paas.web.mapper.ContainerHttpMapper;

@RestController
@RequestMapping("/api/containers")
public class ContainerController {

    private final ListContainersUseCase listContainersUseCase;
    private final StopContainerUseCase stopContainerUseCase;
    private final ContainerHttpMapper containerHttpMapper;

    public ContainerController(ListContainersUseCase listContainersUseCase,
            StopContainerUseCase stopContainerUseCase,
            ContainerHttpMapper containerHttpMapper) {
        this.listContainersUseCase = listContainersUseCase;
        this.stopContainerUseCase = stopContainerUseCase;
        this.containerHttpMapper = containerHttpMapper;
    }

    @GetMapping
    public ResponseEntity<List<ContainerResponse>> list() {
        List<ContainerResponse> response = listContainersUseCase.execute().stream()
                .map(containerHttpMapper::toResponse)
                .toList();
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{containerId}")
    public ResponseEntity<Void> stop(@PathVariable String containerId) {
        stopContainerUseCase.execute(containerId);
        return ResponseEntity.noContent().build();
    }
}
