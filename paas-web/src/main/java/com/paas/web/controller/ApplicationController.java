package com.paas.web.controller;

import java.net.URI;
import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.paas.application.port.in.CreateApplicationUseCase;
import com.paas.application.port.in.GetApplicationUseCase;
import com.paas.application.port.in.ListApplicationsUseCase;
import com.paas.web.dto.request.CreateApplicationRequest;
import com.paas.web.dto.response.ApplicationResponse;
import com.paas.web.mapper.ApplicationHttpMapper;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/applications")
public class ApplicationController {

    private final CreateApplicationUseCase createApplicationUseCase;
    private final ListApplicationsUseCase listApplicationsUseCase;
    private final GetApplicationUseCase getApplicationUseCase;
    private final ApplicationHttpMapper applicationHttpMapper;

    public ApplicationController(CreateApplicationUseCase createApplicationUseCase,
            ListApplicationsUseCase listApplicationsUseCase,
            GetApplicationUseCase getApplicationUseCase,
            ApplicationHttpMapper applicationHttpMapper) {
        this.createApplicationUseCase = createApplicationUseCase;
        this.listApplicationsUseCase = listApplicationsUseCase;
        this.getApplicationUseCase = getApplicationUseCase;
        this.applicationHttpMapper = applicationHttpMapper;
    }

    @PostMapping
    public ResponseEntity<ApplicationResponse> create(@Valid @RequestBody CreateApplicationRequest request) {
        ApplicationResponse response = applicationHttpMapper
                .toResponse(createApplicationUseCase.execute(applicationHttpMapper.toCommand(request)));

        return ResponseEntity.created(URI.create("/api/applications/" + response.id()))
                .body(response);
    }

    @GetMapping
    public ResponseEntity<List<ApplicationResponse>> list() {
        List<ApplicationResponse> response = listApplicationsUseCase.execute().stream()
                .map(applicationHttpMapper::toResponse)
                .toList();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApplicationResponse> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(
                applicationHttpMapper.toResponse(getApplicationUseCase.execute(id)));
    }
}
