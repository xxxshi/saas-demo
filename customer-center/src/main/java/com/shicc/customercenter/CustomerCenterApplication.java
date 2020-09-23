package com.shicc.customercenter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class CustomerCenterApplication {

    public static void main(String[] args) {
        SpringApplication.run(CustomerCenterApplication.class, args);
    }

}
