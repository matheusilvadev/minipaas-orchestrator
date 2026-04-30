package com.paas.infrastructure.persistence.adapter;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.paas.application.port.out.ApplicationRepositoryPort;
import com.paas.domain.model.Application;
import com.paas.domain.valueobject.RepositoryUrl;
import com.paas.infrastructure.persistence.entity.ApplicationEntity;
import com.paas.infrastructure.persistence.repository.SpringDataApplicationJpaRepository;

@Component
public class ApplicationPersistenceAdapter implements ApplicationRepositoryPort {

    private final SpringDataApplicationJpaRepository repository;

    public ApplicationPersistenceAdapter(SpringDataApplicationJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<Application> findAll() {
        return repository.findAll().stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Application> findById(UUID id) {
        return repository.findById(id).map(this::toDomain);
    }

    @Override
    public Application save(Application application) {
        ApplicationEntity entity = toEntity(application);
        ApplicationEntity savedEntity = repository.save(entity);
        return toDomain(savedEntity);
    }

    private ApplicationEntity toEntity(Application domain) {
        return new ApplicationEntity(
                domain.id(),
                domain.name(),
                domain.repositoryUrl().value(),
                domain.branchName());
    }

    private Application toDomain(ApplicationEntity entity) {
        return new Application(
                entity.getId(),
                entity.getName(),
                new RepositoryUrl(entity.getRepositoryUrl()),
                entity.getBranch());
    }

}
