package com.paas.bootstrap;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import com.paas.application.port.in.CreateApplicationUseCase;
import com.paas.application.port.in.GetDeploymentLogsUseCase;
import com.paas.application.port.in.StartDeploymentUseCase;
import com.paas.web.controller.ApplicationController;
import com.paas.web.controller.ContainerController;
import com.paas.web.controller.DeploymentController;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:paas-bootstrap;MODE=PostgreSQL;DB_CLOSE_DELAY=-1",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect",
        "spring.thymeleaf.check-template-location=false",
        "SPRING_SECURITY_USER_NAME=admin",
        "SPRING_SECURITY_USER_PASSWORD=admin",
        "paas.deploy.base-dir=/tmp/paas-test/workspaces",
        "paas.port.min=18080",
        "paas.port.max=18100"
})
class ApplicationContextLoadsTest {

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private ApplicationController applicationController;

    @Autowired
    private DeploymentController deploymentController;

    @Autowired
    private ContainerController containerController;

    @Autowired
    private CreateApplicationUseCase createApplicationUseCase;

    @Autowired
    private StartDeploymentUseCase startDeploymentUseCase;

    @Autowired
    private GetDeploymentLogsUseCase getDeploymentLogsUseCase;

    @Test
    void shouldLoadFullApplicationContext() {
        assertThat(applicationContext).isNotNull();
        assertThat(applicationController).isNotNull();
        assertThat(deploymentController).isNotNull();
        assertThat(containerController).isNotNull();
        assertThat(createApplicationUseCase).isNotNull();
        assertThat(startDeploymentUseCase).isNotNull();
        assertThat(getDeploymentLogsUseCase).isNotNull();
    }
}
