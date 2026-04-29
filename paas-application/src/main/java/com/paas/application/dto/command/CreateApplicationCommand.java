package com.paas.application.dto.command;

public record CreateApplicationCommand(String name, String repositoryUrl, String branchName) {

}
