package com.paas.infrastructure.docker;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import org.springframework.stereotype.Component;

@Component
public class DockerCommandExecutor {

    public void build(String imageName, File contextDir) {
        execute("docker", "build", "-t", imageName, ".", contextDir);
    }

    public String run(String imageName, int port) {
        String containerName = imageName + "-" + System.currentTimeMillis();
        execute("docker", "run", "-d", "--name", containerName, "-p", port + ":8080", imageName);
        return containerName;
    }

    public void stop(String containerId) {
        execute("docker", "stop", containerId);
        execute("docker", "rm", containerId);
    }

    /**
     * Executa comandos usando Object... para aceitar Strings de comando
     * e opcionalmente um File no final para o working directory.
     */
    private void execute(Object... args) {
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

            pb.inheritIO();
            Process process = pb.start();

            boolean finished = process.waitFor(10, TimeUnit.MINUTES);
            if (!finished || process.exitValue() != 0) {
                throw new RuntimeException("Erro executing Docker command: " + String.join(" ", command));
            }

        } catch (IOException | InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Docker process execution failed.", e);
        }
    }

}
