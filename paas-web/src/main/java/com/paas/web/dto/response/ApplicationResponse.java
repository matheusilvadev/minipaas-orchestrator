package com.paas.web.dto.response;

import java.util.UUID;

public record ApplicationResponse(UUID id, String name, String repositoryUrl) {
}
