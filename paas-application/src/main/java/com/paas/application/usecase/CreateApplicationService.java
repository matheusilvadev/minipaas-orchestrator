package com.paas.application.usecase;

import java.util.UUID;

import com.paas.application.dto.command.CreateApplicationCommand;
import com.paas.application.dto.result.ApplicationResult;
import com.paas.application.port.in.CreateApplicationUseCase;
import com.paas.application.port.out.ApplicationRepositoryPort;
import com.paas.domain.model.Application;
import com.paas.domain.valueobject.RepositoryUrl;

public class CreateApplicationService implements CreateApplicationUseCase {
    private final ApplicationRepositoryPort applicationRepositoryPort;

    public CreateApplicationService(ApplicationRepositoryPort applicationRepositoryPort) {
        this.applicationRepositoryPort = applicationRepositoryPort;
    }

    @Override
    public ApplicationResult execute(CreateApplicationCommand command) {
        Application application = new Application(
                UUID.randomUUID(),
                command.name(),
                new RepositoryUrl(command.repositoryUrl()),
                command.branchName());

        Application savedApplication = applicationRepositoryPort.save(application);

        return new ApplicationResult(savedApplication.id(), savedApplication.name(),
                savedApplication.repositoryUrl().value());
    }
}
