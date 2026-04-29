package com.paas.application.usecase;

import com.paas.application.dto.result.ApplicationResult;
import com.paas.application.exception.ApplicationNotFoundException;
import com.paas.application.port.out.ApplicationRepositoryPort;
import com.paas.domain.model.Application;
import com.paas.domain.valueobject.RepositoryUrl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetApplicationServiceTest {

    @Mock
    private ApplicationRepositoryPort applicationRepositoryPort;

    @InjectMocks
    private GetApplicationService getApplicationService;

    @Test
    void shouldReturnApplicationWhenItExists() {
        UUID applicationId = UUID.randomUUID();
        Application application = new Application(
                applicationId,
                "payments-api",
                new RepositoryUrl("https://github.com/example/payments-api.git"),
                "main"
        );

        when(applicationRepositoryPort.findById(applicationId)).thenReturn(Optional.of(application));

        ApplicationResult result = getApplicationService.execute(applicationId);

        assertThat(result.id()).isEqualTo(applicationId);
        assertThat(result.name()).isEqualTo("payments-api");
        assertThat(result.repositoryUrl()).isEqualTo("https://github.com/example/payments-api.git");
    }

    @Test
    void shouldThrowExceptionWhenApplicationDoesNotExist() {
        UUID applicationId = UUID.randomUUID();

        when(applicationRepositoryPort.findById(applicationId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> getApplicationService.execute(applicationId))
                .isInstanceOf(ApplicationNotFoundException.class);
    }
}
