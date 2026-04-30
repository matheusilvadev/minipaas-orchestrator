package com.paas.infrastructure.git;

import java.io.File;

import org.springframework.stereotype.Component;

import com.paas.application.port.out.GitClientPort;
import com.paas.domain.valueobject.RepositoryUrl;

@Component
public class GitClientAdapter implements GitClientPort {

    private final GitCliClient gitCliClient;

    public GitClientAdapter(GitCliClient gitCliClient) {
        this.gitCliClient = gitCliClient;
    }

    @Override
    public File clone(RepositoryUrl url, String branch, File destination) {
        String urlString = url.value();
        return gitCliClient.executeClone(urlString, branch, destination);
    }

}
