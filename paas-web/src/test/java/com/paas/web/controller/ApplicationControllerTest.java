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

import com.paas.application.dto.command.CreateApplicationCommand;
import com.paas.application.dto.result.ApplicationResult;
import com.paas.application.exception.ApplicationNotFoundException;
import com.paas.application.port.in.CreateApplicationUseCase;
import com.paas.application.port.in.GetApplicationUseCase;
import com.paas.application.port.in.ListApplicationsUseCase;
import com.paas.web.exception.RestExceptionHandler;
import com.paas.web.mapper.ApplicationHttpMapper;
import com.paas.web.security.SecurityConfig;

@WebMvcTest(ApplicationController.class)
@Import({ RestExceptionHandler.class, SecurityConfig.class, ApplicationHttpMapper.class })
class ApplicationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CreateApplicationUseCase createApplicationUseCase;

    @MockitoBean
    private ListApplicationsUseCase listApplicationsUseCase;

    @MockitoBean
    private GetApplicationUseCase getApplicationUseCase;

    @Test
    void shouldCreateApplicationWhenRequestIsValid() throws Exception {
        UUID applicationId = UUID.randomUUID();
        when(createApplicationUseCase.execute(any(CreateApplicationCommand.class)))
                .thenReturn(new ApplicationResult(applicationId, "my-app", "https://github.com/example/my-app.git"));

        mockMvc.perform(post("/api/applications")
                .with(httpBasic("admin", "admin"))
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                          "name": "my-app",
                          "repositoryUrl": "https://github.com/example/my-app.git",
                          "branchName": "main"
                        }
                        """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(applicationId.toString()))
                .andExpect(jsonPath("$.name").value("my-app"))
                .andExpect(jsonPath("$.repositoryUrl").value("https://github.com/example/my-app.git"));

        verify(createApplicationUseCase)
                .execute(new CreateApplicationCommand("my-app", "https://github.com/example/my-app.git", "main"));
    }

    @Test
    void shouldRejectCreateApplicationWhenRequestIsInvalid() throws Exception {
        mockMvc.perform(post("/api/applications")
                .with(httpBasic("admin", "admin"))
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                          "name": "",
                          "repositoryUrl": "",
                          "branchName": "main"
                        }
                        """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.errors.name").exists())
                .andExpect(jsonPath("$.errors.repositoryUrl").exists());
    }

    @Test
    void shouldListApplications() throws Exception {
        UUID firstId = UUID.randomUUID();
        UUID secondId = UUID.randomUUID();
        when(listApplicationsUseCase.execute()).thenReturn(List.of(
                new ApplicationResult(firstId, "app-one", "https://github.com/example/app-one.git"),
                new ApplicationResult(secondId, "app-two", "https://github.com/example/app-two.git")));

        mockMvc.perform(get("/api/applications")
                .with(httpBasic("admin", "admin")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(firstId.toString()))
                .andExpect(jsonPath("$[0].name").value("app-one"))
                .andExpect(jsonPath("$[1].id").value(secondId.toString()))
                .andExpect(jsonPath("$[1].name").value("app-two"));
    }

    @Test
    void shouldGetApplicationById() throws Exception {
        UUID applicationId = UUID.randomUUID();
        when(getApplicationUseCase.execute(applicationId))
                .thenReturn(new ApplicationResult(applicationId, "my-app", "https://github.com/example/my-app.git"));

        mockMvc.perform(get("/api/applications/{id}", applicationId)
                .with(httpBasic("admin", "admin")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(applicationId.toString()))
                .andExpect(jsonPath("$.name").value("my-app"))
                .andExpect(jsonPath("$.repositoryUrl").value("https://github.com/example/my-app.git"));
    }

    @Test
    void shouldReturnNotFoundWhenApplicationDoesNotExist() throws Exception {
        UUID applicationId = UUID.randomUUID();
        when(getApplicationUseCase.execute(applicationId))
                .thenThrow(new ApplicationNotFoundException("Application not found"));

        mockMvc.perform(get("/api/applications/{id}", applicationId)
                .with(httpBasic("admin", "admin")))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Application not found"));
    }

    @Test
    void shouldReturnBadRequestWhenApplicationIdIsInvalid() throws Exception {
        mockMvc.perform(get("/api/applications/{id}", "not-a-uuid")
                .with(httpBasic("admin", "admin")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Invalid request parameter: id"));
    }
}
