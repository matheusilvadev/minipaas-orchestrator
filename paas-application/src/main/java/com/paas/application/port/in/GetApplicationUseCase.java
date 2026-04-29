package com.paas.application.port.in;

import java.util.UUID;

import com.paas.application.dto.result.ApplicationResult;

public interface GetApplicationUseCase {
    ApplicationResult execute(UUID id);
}
