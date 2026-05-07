package com.paas.web.mapper;

import org.springframework.stereotype.Component;

import com.paas.application.dto.command.CreateApplicationCommand;
import com.paas.application.dto.result.ApplicationResult;
import com.paas.web.dto.request.CreateApplicationRequest;
import com.paas.web.dto.response.ApplicationResponse;

@Component
public class ApplicationHttpMapper {

    public CreateApplicationCommand toCommand(CreateApplicationRequest request) {
        return new CreateApplicationCommand(request.name(), request.repositoryUrl(), request.branchName());
    }

    public ApplicationResponse toResponse(ApplicationResult result) {
        return new ApplicationResponse(result.id(), result.name(), result.repositoryUrl());
    }
}
