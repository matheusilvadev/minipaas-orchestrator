package com.paas.application.port.in;

import com.paas.application.dto.command.CreateApplicationCommand;
import com.paas.application.dto.result.ApplicationResult;

public interface CreateApplicationUseCase {
    ApplicationResult execute(CreateApplicationCommand command);
}
