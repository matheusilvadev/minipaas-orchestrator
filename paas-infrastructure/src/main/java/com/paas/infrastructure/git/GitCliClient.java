package com.paas.infrastructure.git;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import org.springframework.stereotype.Component;

@Component
public class GitCliClient {
    public File executeClone(String url, String branch, File destination) {
        return executeClone(url, branch, destination, null);
    }

    public File executeClone(String url, String branch, File destination, Consumer<String> logConsumer) {
        try {
            ProcessBuilder pb = new ProcessBuilder(
                    "git", "clone", "--branch", branch, "--depth", "1", url, ".");

            pb.directory(destination);
            pb.redirectErrorStream(true);

            Process process = pb.start();
            streamOutput(process.getInputStream(), logConsumer);

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

    private void streamOutput(InputStream inputStream, Consumer<String> logConsumer) throws IOException {
        if (logConsumer == null) {
            inputStream.transferTo(OutputStream.nullOutputStream());
            return;
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = reader.readLine()) != null) {
                logConsumer.accept(line);
            }
        }
    }
}
