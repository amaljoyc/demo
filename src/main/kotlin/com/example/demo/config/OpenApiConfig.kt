package com.example.demo.config

import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.security.SecurityScheme
import io.swagger.v3.oas.models.servers.Server
import org.springdoc.core.GroupedOpenApi
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component

@Component
class OpenApiConfig {

    @Bean
    fun openAPI(
        @Value("\${springdoc.version}") appVersion: String?,
        @Value("\${server.servlet.context-path}") contextPath: String?
    ): OpenAPI = OpenAPI()
        .addServersItem(Server().url(contextPath))
        .info(
            Info().title("Demo App")
                .description("sample demo app")
                .version(appVersion)
        )
        .components(
            Components()
                .addSecuritySchemes(
                    "basicScheme",
                    SecurityScheme().type(SecurityScheme.Type.HTTP).scheme("basic")
                )
        )

    @Bean
    fun greetApis(): GroupedOpenApi =
        GroupedOpenApi.builder()
            .group("greet").pathsToMatch("/greet/**")
            .build()

    @Bean
    fun maintenanceApis(): GroupedOpenApi =
        GroupedOpenApi.builder()
            .group("maintenance").pathsToMatch("/maintenance/**")
            .build()
}
