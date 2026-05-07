package com.paas.web.controller;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.paas.application.dto.result.ContainerResult;
import com.paas.application.port.in.ListContainersUseCase;
import com.paas.application.port.in.StopContainerUseCase;
import com.paas.web.exception.RestExceptionHandler;
import com.paas.web.mapper.ContainerHttpMapper;
import com.paas.web.security.SecurityConfig;

@WebMvcTest(ContainerController.class)
@Import({ RestExceptionHandler.class, SecurityConfig.class, ContainerHttpMapper.class })
class ContainerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ListContainersUseCase listContainersUseCase;

    @MockitoBean
    private StopContainerUseCase stopContainerUseCase;

    @Test
    void shouldListRunningContainers() throws Exception {
        UUID containerId = UUID.randomUUID();
        UUID deploymentId = UUID.randomUUID();
        when(listContainersUseCase.execute()).thenReturn(List.of(
                new ContainerResult(containerId, deploymentId, "container-123", 8080)));

        mockMvc.perform(get("/api/containers")
                .with(httpBasic("admin", "admin")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(containerId.toString()))
                .andExpect(jsonPath("$[0].deploymentId").value(deploymentId.toString()))
                .andExpect(jsonPath("$[0].containerId").value("container-123"))
                .andExpect(jsonPath("$[0].port").value(8080));
    }

    @Test
    void shouldStopContainer() throws Exception {
        mockMvc.perform(delete("/api/containers/{containerId}", "container-123")
                .with(httpBasic("admin", "admin")))
                .andExpect(status().isNoContent());

        verify(stopContainerUseCase).execute("container-123");
    }
}
