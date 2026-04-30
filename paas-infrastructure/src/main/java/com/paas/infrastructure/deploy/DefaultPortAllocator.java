package com.paas.infrastructure.deploy;

import java.net.ServerSocket;
import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.paas.application.port.out.PortAllocatorPort;
import com.paas.domain.valueobject.PortNumber;

@Component
public class DefaultPortAllocator implements PortAllocatorPort {

    private final int minPort;
    private final int maxPort;
    private final Set<Integer> allocatedPorts = new HashSet<>();

    public DefaultPortAllocator(
            @Value("${paas.port.min:8080}") int minPort,
            @Value("${paas.port.max:9000}") int maxPort) {
        this.minPort = minPort;
        this.maxPort = maxPort;
    }

    @Override
    public synchronized PortNumber allocate() {
        for (int port = minPort; port <= maxPort; port++) {
            if (!allocatedPorts.contains(port) && isSystemPortAvailable(port)) {
                allocatedPorts.add(port);
                return new PortNumber(port);
            }
        }
        throw new RuntimeException("Nenhuma porta disponível no intervalo: " + minPort + "-" + maxPort);
    }

    @Override
    public synchronized void release(PortNumber port) {
        allocatedPorts.remove(port.value());
    }

    private boolean isSystemPortAvailable(int port) {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            serverSocket.setReuseAddress(true);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

}
