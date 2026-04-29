package com.paas.application.usecase;

import com.paas.application.dto.result.ApplicationResult;
import com.paas.application.port.out.ApplicationRepositoryPort;
import com.paas.domain.model.Application;
import com.paas.domain.valueobject.RepositoryUrl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ListApplicationsServiceTest {

    @Mock
    private ApplicationRepositoryPort applicationRepositoryPort;

    @InjectMocks
    private ListApplicationsService listApplicationsService;

    @Test
    void shouldListApplicationsSuccessfully() {
        UUID firstId = UUID.randomUUID();
        UUID secondId = UUID.randomUUID();

        when(applicationRepositoryPort.findAll()).thenReturn(List.of(
                new Application(
                        firstId,
                        "payments-api",
                        new RepositoryUrl("https://github.com/example/payments-api.git"),
                        "main"
                ),
                new Application(
                        secondId,
                        "checkout-api",
                        new RepositoryUrl("https://github.com/example/checkout-api.git"),
                        "develop"
                )
        ));

        List<ApplicationResult> result = listApplicationsService.execute();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).id()).isEqualTo(firstId);
        assertThat(result.get(0).name()).isEqualTo("payments-api");
        assertThat(result.get(1).id()).isEqualTo(secondId);
        assertThat(result.get(1).name()).isEqualTo("checkout-api");
    }
}
