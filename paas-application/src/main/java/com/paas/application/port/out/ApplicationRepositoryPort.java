package com.paas.application.port.out;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.paas.domain.model.Application;

public interface ApplicationRepositoryPort {
    Application save(Application application);

    Optional<Application> findById(UUID id);

    List<Application> findAll();
}
