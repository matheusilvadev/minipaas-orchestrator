package com.paas.application.usecase;

import com.paas.application.dto.command.CreateApplicationCommand;
import com.paas.application.dto.result.ApplicationResult;
import com.paas.application.port.out.ApplicationRepositoryPort;
import com.paas.domain.exception.InvalidRepositoryException;
import com.paas.domain.model.Application;
import com.paas.domain.valueobject.RepositoryUrl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateApplicationServiceTest {

        @Mock
        private ApplicationRepositoryPort applicationRepositoryPort;

        @InjectMocks
        private CreateApplicationService createApplicationService;

        @Test
        void shouldCreateApplicationAndReturnGeneratedId() {
                UUID generatedId = UUID.randomUUID();
                CreateApplicationCommand command = new CreateApplicationCommand(
                                "my-service",
                                "https://github.com/example/my-service.git",
                                "main");

                when(applicationRepositoryPort.save(any(Application.class)))
                                .thenReturn(new Application(
                                                generatedId,
                                                "my-service",
                                                new RepositoryUrl("https://github.com/example/my-service.git"),
                                                "main"));

                ApplicationResult result = createApplicationService.execute(command);

                ArgumentCaptor<Application> applicationCaptor = ArgumentCaptor.forClass(Application.class);
                verify(applicationRepositoryPort).save(applicationCaptor.capture());

                Application applicationToPersist = applicationCaptor.getValue();

                assertThat(applicationToPersist.name()).isEqualTo("my-service");
                assertThat(applicationToPersist.repositoryUrl())
                                .isEqualTo(new RepositoryUrl("https://github.com/example/my-service.git"));
                assertThat(applicationToPersist.branchName()).isEqualTo("main");
                assertThat(result.id()).isEqualTo(generatedId);
        }

        @Test
        void shouldRejectApplicationCreationWhenRepositoryUrlIsInvalid() {
                CreateApplicationCommand command = new CreateApplicationCommand(
                                "my-service",
                                "invalid-url",
                                "main");

                assertThatThrownBy(() -> createApplicationService.execute(command))
                                .isInstanceOf(InvalidRepositoryException.class);

                verify(applicationRepositoryPort, never()).save(any(Application.class));
        }
}
