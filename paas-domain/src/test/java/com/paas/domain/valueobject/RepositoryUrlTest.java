package com.paas.domain.valueobject;

import com.paas.domain.exception.InvalidRepositoryException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RepositoryUrlTest {

    @Test
    void shouldCreateRepositoryUrlWhenUrlIsValid() {
        RepositoryUrl repositoryUrl = new RepositoryUrl("https://github.com/example/my-service.git");

        assertThat(repositoryUrl).isNotNull();
        assertThat(repositoryUrl.value()).isEqualTo("https://github.com/example/my-service.git");
    }

    @Test
    void shouldRejectRepositoryUrlWhenUrlIsInvalid() {
        assertThatThrownBy(() -> new RepositoryUrl("invalid-url"))
                .isInstanceOf(InvalidRepositoryException.class);
    }
}
