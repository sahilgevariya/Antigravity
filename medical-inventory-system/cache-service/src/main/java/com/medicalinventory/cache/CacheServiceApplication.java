package com.medicalinventory.cache;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication(scanBasePackages = {"com.medicalinventory.cache", "com.medicalinventory.common"})
@EnableDiscoveryClient
public class CacheServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(CacheServiceApplication.class, args);
    }
}
