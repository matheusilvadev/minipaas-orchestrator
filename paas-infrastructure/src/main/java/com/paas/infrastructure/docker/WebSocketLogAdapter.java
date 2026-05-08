package com.paas.infrastructure.docker;

import java.util.UUID;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import com.paas.application.port.out.DeploymentLogPublisherPort;

@Component
public class WebSocketLogAdapter implements DeploymentLogPublisherPort {

    private final SimpMessagingTemplate messagingTemplate;

    public WebSocketLogAdapter(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @Override
    public void publish(UUID deploymentId, String message) {
        messagingTemplate.convertAndSend("/topic/deployments/" + deploymentId + "/logs", message);
    }
}
