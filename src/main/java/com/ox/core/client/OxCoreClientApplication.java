package com.ox.core.client;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;

@SpringBootApplication
@OpenAPIDefinition(
    info = @Info(
        title = "OX Core Client API",
        version = "1.0.0",
        description = "API for managing bank clients and their accounts"
    )
)
public class OxCoreClientApplication {
    public static void main(String[] args) {
        SpringApplication.run(OxCoreClientApplication.class, args);
    }
    
}
