package com.paas.web.security;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.paas.application.port.in.CreateApplicationUseCase;
import com.paas.application.port.in.GetApplicationUseCase;
import com.paas.application.port.in.ListApplicationsUseCase;
import com.paas.web.controller.ApplicationController;
import com.paas.web.exception.RestExceptionHandler;
import com.paas.web.mapper.ApplicationHttpMapper;

@WebMvcTest(ApplicationController.class)
@Import({ SecurityConfig.class, RestExceptionHandler.class, ApplicationHttpMapper.class })
class SecurityConfigTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CreateApplicationUseCase createApplicationUseCase;

    @MockitoBean
    private ListApplicationsUseCase listApplicationsUseCase;

    @MockitoBean
    private GetApplicationUseCase getApplicationUseCase;

    @Test
    void shouldRequireAuthentication() throws Exception {
        mockMvc.perform(get("/api/applications"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldAllowAuthenticatedRequest() throws Exception {
        org.mockito.Mockito.when(listApplicationsUseCase.execute()).thenReturn(List.of());

        mockMvc.perform(get("/api/applications")
                .with(httpBasic("admin", "admin")))
                .andExpect(status().isOk());
    }
}
