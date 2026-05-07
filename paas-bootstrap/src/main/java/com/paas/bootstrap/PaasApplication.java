package com.paas.bootstrap;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication(scanBasePackages = "com.paas")
@EnableAsync
public class PaasApplication {

    public static void main(String[] args) {
        SpringApplication.run(PaasApplication.class, args);
    }
}
