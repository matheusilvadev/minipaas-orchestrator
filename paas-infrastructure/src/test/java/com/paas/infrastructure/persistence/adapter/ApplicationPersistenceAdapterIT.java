package com.paas.infrastructure.persistence.adapter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import com.paas.domain.model.Application;
import com.paas.domain.valueobject.RepositoryUrl;
import com.paas.infrastructure.persistence.repository.SpringDataApplicationJpaRepository;

@DataJpaTest
@Import({ ApplicationPersistenceAdapter.class })
public class ApplicationPersistenceAdapterIT {

    @Autowired
    private ApplicationPersistenceAdapter adapter;

    @Autowired
    private SpringDataApplicationJpaRepository jpaRepository;

    @Test
    @DisplayName("Deve salvar uma aplicação de domínio com sucesso")
    void shouldSaveApplication() {
        // Arrange
        Application application = new Application(
                UUID.randomUUID(),
                "minipaas-api",
                new RepositoryUrl("https://github.com/user/repo.git"),
                "main");

        // Act
        Application savedApp = adapter.save(application);

        // Assert
        assertNotNull(savedApp.id());
        assertEquals("minipaas-api", savedApp.name());
        assertTrue(jpaRepository.findById(savedApp.id()).isPresent());
    }
}
