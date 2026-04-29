package com.paas.application.port.in;

import java.util.List;

import com.paas.application.dto.result.ContainerResult;

public interface ListContainersUseCase {
    List<ContainerResult> execute();
}
