package com.shicc.saas.tenant;

import com.shicc.saas.tenant.config.ApplicationProperties;
import com.shicc.saas.tenant.service.TopicMessageService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableConfigurationProperties({ApplicationProperties.class})
@EnableDiscoveryClient
@ComponentScan(basePackages = {"com.shicc.*"})
public class SaasTenantApplication {

    public static void main(String[] args) {
        SpringApplication.run(SaasTenantApplication.class, args);
    }

}
