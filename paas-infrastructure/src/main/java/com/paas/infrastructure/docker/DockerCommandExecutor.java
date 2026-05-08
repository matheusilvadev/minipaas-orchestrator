package com.paas.infrastructure.docker;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import org.springframework.stereotype.Component;

@Component
public class DockerCommandExecutor {

    public void build(String imageName, File contextDir) {
        execute(null, "docker", "build", "-t", imageName, ".", contextDir);
    }

    public void build(String imageName, File contextDir, Consumer<String> logConsumer) {
        execute(logConsumer, "docker", "build", "-t", imageName, ".", contextDir);
    }

    public String run(String imageName, int port) {
        String containerName = imageName + "-" + System.currentTimeMillis();
        execute(null, "docker", "run", "-d", "--name", containerName, "-p", port + ":8080", imageName);
        return containerName;
    }

    public void stop(String containerId) {
        execute(null, "docker", "stop", containerId);
        execute(null, "docker", "rm", containerId);
    }

    private void execute(Consumer<String> logConsumer, Object... args) {
        try {
            File workingDir = null;
            int commandLength = args.length;

            if (args[args.length - 1] instanceof File) {
                workingDir = (File) args[args.length - 1];
                commandLength--;
            }

            String[] command = Arrays.stream(args)
                    .limit(commandLength)
                    .map(Object::toString)
                    .toArray(String[]::new);

            ProcessBuilder pb = new ProcessBuilder(command);
            if (workingDir != null) {
                pb.directory(workingDir);
            }

            pb.redirectErrorStream(true);
            Process process = pb.start();
            streamOutput(process.getInputStream(), logConsumer);

            boolean finished = process.waitFor(10, TimeUnit.MINUTES);
            if (!finished || process.exitValue() != 0) {
                throw new RuntimeException("Erro executing Docker command: " + String.join(" ", command));
            }

        } catch (IOException | InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Docker process execution failed.", e);
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
