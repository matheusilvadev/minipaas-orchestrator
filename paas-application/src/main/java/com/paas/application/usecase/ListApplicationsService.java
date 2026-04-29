package com.paas.application.usecase;

import java.util.List;
import java.util.stream.Collectors;

import com.paas.application.dto.result.ApplicationResult;
import com.paas.application.port.in.ListApplicationsUseCase;
import com.paas.application.port.out.ApplicationRepositoryPort;

public class ListApplicationsService implements ListApplicationsUseCase {

    private final ApplicationRepositoryPort applicationRepositoryPort;

    public ListApplicationsService(ApplicationRepositoryPort applicationRepositoryPort) {
        this.applicationRepositoryPort = applicationRepositoryPort;
    }

    @Override
    public List<ApplicationResult> execute() {
        return applicationRepositoryPort.findAll().stream()
                .map(app -> new ApplicationResult(
                        app.id(),
                        app.name(),
                        app.repositoryUrl().value()))
                .collect(Collectors.toList());
    }

}
