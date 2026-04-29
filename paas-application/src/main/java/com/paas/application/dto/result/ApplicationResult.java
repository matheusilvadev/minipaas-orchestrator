package com.paas.application.dto.result;

import java.util.UUID;

public record ApplicationResult(UUID id, String name, String repositoryUrl) {

}
