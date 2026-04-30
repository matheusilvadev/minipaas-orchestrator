package com.paas.infrastructure.docker;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.function.Consumer;

import org.springframework.stereotype.Component;

@Component
public class DockerLogReader {

    /**
     * Segue os logs de um container e os envia para um callback.
     */
    public void readLogs(String containerId, Consumer<String> logConsumer) {
        new Thread(() -> {
            try {
                ProcessBuilder pb = new ProcessBuilder("docker", "logs", "-f", containerId);
                Process process = pb.start();

                try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        logConsumer.accept(line);
                    }
                }
                process.waitFor();
            } catch (Exception e) {
                logConsumer.accept("ERRO AO LER LOGS: " + e.getMessage());
            }
        }).start();
    }
}
