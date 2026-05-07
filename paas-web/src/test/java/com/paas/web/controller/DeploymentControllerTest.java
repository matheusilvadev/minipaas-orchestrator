package com.paas.web.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.paas.application.dto.command.StartDeploymentCommand;
import com.paas.application.dto.result.DeploymentLogResult;
import com.paas.application.dto.result.DeploymentResult;
import com.paas.application.exception.DeploymentFailedException;
import com.paas.application.exception.DeploymentNotFoundException;
import com.paas.application.port.in.GetDeploymentLogsUseCase;
import com.paas.application.port.in.GetDeploymentUseCase;
import com.paas.application.port.in.ListDeploymentsUseCase;
import com.paas.application.port.in.StartDeploymentUseCase;
import com.paas.web.exception.RestExceptionHandler;
import com.paas.web.mapper.DeploymentHttpMapper;
import com.paas.web.security.SecurityConfig;

@WebMvcTest(DeploymentController.class)
@Import({ RestExceptionHandler.class, SecurityConfig.class, DeploymentHttpMapper.class })
class DeploymentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private StartDeploymentUseCase startDeploymentUseCase;

    @MockitoBean
    private ListDeploymentsUseCase listDeploymentsUseCase;

    @MockitoBean
    private GetDeploymentUseCase getDeploymentUseCase;

    @MockitoBean
    private GetDeploymentLogsUseCase getDeploymentLogsUseCase;

    @Test
    void shouldStartDeploymentWhenRequestIsValid() throws Exception {
        UUID applicationId = UUID.randomUUID();
        UUID deploymentId = UUID.randomUUID();
        when(startDeploymentUseCase.execute(any(StartDeploymentCommand.class)))
                .thenReturn(new DeploymentResult(deploymentId, "RUNNING"));

        mockMvc.perform(post("/api/deployments")
                .with(httpBasic("admin", "admin"))
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                          "applicationId": "%s"
                        }
                        """.formatted(applicationId)))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.id").value(deploymentId.toString()))
                .andExpect(jsonPath("$.status").value("RUNNING"));

        verify(startDeploymentUseCase).execute(new StartDeploymentCommand(applicationId));
    }

    @Test
    void shouldRejectDeploymentWhenApplicationIdIsMissing() throws Exception {
        mockMvc.perform(post("/api/deployments")
                .with(httpBasic("admin", "admin"))
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                        }
                        """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.errors.applicationId").exists());
    }

    @Test
    void shouldReturnConflictWhenDeploymentFails() throws Exception {
        UUID applicationId = UUID.randomUUID();
        when(startDeploymentUseCase.execute(new StartDeploymentCommand(applicationId)))
                .thenThrow(new DeploymentFailedException("Deployment already in progress"));

        mockMvc.perform(post("/api/deployments")
                .with(httpBasic("admin", "admin"))
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                          "applicationId": "%s"
                        }
                        """.formatted(applicationId)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Deployment already in progress"));
    }

    @Test
    void shouldListDeploymentsByApplicationId() throws Exception {
        UUID applicationId = UUID.randomUUID();
        UUID firstDeploymentId = UUID.randomUUID();
        UUID secondDeploymentId = UUID.randomUUID();

        when(listDeploymentsUseCase.execute(applicationId)).thenReturn(List.of(
                new DeploymentResult(firstDeploymentId, "PENDING"),
                new DeploymentResult(secondDeploymentId, "RUNNING")));

        mockMvc.perform(get("/api/applications/{applicationId}/deployments", applicationId)
                .with(httpBasic("admin", "admin")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(firstDeploymentId.toString()))
                .andExpect(jsonPath("$[0].status").value("PENDING"))
                .andExpect(jsonPath("$[1].id").value(secondDeploymentId.toString()))
                .andExpect(jsonPath("$[1].status").value("RUNNING"));
    }

    @Test
    void shouldGetDeploymentById() throws Exception {
        UUID deploymentId = UUID.randomUUID();
        when(getDeploymentUseCase.execute(deploymentId))
                .thenReturn(new DeploymentResult(deploymentId, "BUILDING"));

        mockMvc.perform(get("/api/deployments/{deploymentId}", deploymentId)
                .with(httpBasic("admin", "admin")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(deploymentId.toString()))
                .andExpect(jsonPath("$.status").value("BUILDING"));
    }

    @Test
    void shouldReturnNotFoundWhenDeploymentDoesNotExist() throws Exception {
        UUID deploymentId = UUID.randomUUID();
        when(getDeploymentUseCase.execute(deploymentId))
                .thenThrow(new DeploymentNotFoundException("Deployment not found"));

        mockMvc.perform(get("/api/deployments/{deploymentId}", deploymentId)
                .with(httpBasic("admin", "admin")))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Deployment not found"));
    }

    @Test
    void shouldGetDeploymentLogs() throws Exception {
        UUID deploymentId = UUID.randomUUID();
        when(getDeploymentLogsUseCase.execute(deploymentId))
                .thenReturn(new DeploymentLogResult(deploymentId, List.of("cloning", "building", "running")));

        mockMvc.perform(get("/api/deployments/{deploymentId}/logs", deploymentId)
                .with(httpBasic("admin", "admin")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.deploymentId").value(deploymentId.toString()))
                .andExpect(jsonPath("$.lines[0]").value("cloning"))
                .andExpect(jsonPath("$.lines[2]").value("running"));
    }

    @Test
    void shouldReturnBadRequestWhenDeploymentIdIsInvalid() throws Exception {
        mockMvc.perform(get("/api/deployments/{deploymentId}", "invalid-id")
                .with(httpBasic("admin", "admin")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Invalid request parameter: deploymentId"));
    }
}
