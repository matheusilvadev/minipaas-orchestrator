package com.paas.infrastructure.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EntityScan("com.paas.infrastructure.persistence.entity")
@EnableJpaRepositories("com.paas.infrastructure.persistence.repository")
public class JpaConfig {

}
