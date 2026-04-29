package com.paas.application.port.out;

import java.util.List;

import com.paas.domain.model.ContainerInstance;

public interface ContainerRepositoryPort {
    List<ContainerInstance> findAll();
}
