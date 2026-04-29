package com.paas.application.usecase;

import java.util.UUID;

import com.paas.application.dto.result.ApplicationResult;
import com.paas.application.exception.ApplicationNotFoundException;
import com.paas.application.port.in.GetApplicationUseCase;
import com.paas.application.port.out.ApplicationRepositoryPort;
import com.paas.domain.model.Application;

public class GetApplicationService implements GetApplicationUseCase {

    private final ApplicationRepositoryPort applicationRepositoryPort;

    public GetApplicationService(ApplicationRepositoryPort applicationRepositoryPort) {
        this.applicationRepositoryPort = applicationRepositoryPort;
    }

    @Override
    public ApplicationResult execute(UUID id) {
        Application application = applicationRepositoryPort.findById(id)
                .orElseThrow(() -> new ApplicationNotFoundException("Application not found with ID:" + id));

        return new ApplicationResult(
                application.id(),
                application.name(),
                application.repositoryUrl().value());
    }

}
