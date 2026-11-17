package com.fuentes.ventas.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// Configuración básica de Swagger / OpenAPI
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API de Microservicio de Ventas")
                        .version("1.0")
                        .description("Documentación de endpoints para los módulos de Productos y Ventas con PostgreSQL"));
    }
}
