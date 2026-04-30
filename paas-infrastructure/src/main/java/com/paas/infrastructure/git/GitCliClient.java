package com.paas.infrastructure.git;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.springframework.stereotype.Component;

@Component
public class GitCliClient {
    public File executeClone(String url, String branch, File destination) {
        try {
            // No Ubuntu do seu notebook, o processo será disparado aqui
            ProcessBuilder pb = new ProcessBuilder(
                    "git", "clone", "--branch", branch, "--depth", "1", url, ".");

            pb.directory(destination);
            pb.inheritIO(); // Importante para você ver o progresso no console do servidor

            Process process = pb.start();
            boolean finished = process.waitFor(5, TimeUnit.MINUTES);

            if (!finished || process.exitValue() != 0) {
                throw new RuntimeException(
                        "Falha ao clonar repositório Git no diretório: " + destination.getAbsolutePath());
            }

            return destination;
        } catch (IOException | InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Erro de I/O ao executar git clone", e);
        }
    }
}
