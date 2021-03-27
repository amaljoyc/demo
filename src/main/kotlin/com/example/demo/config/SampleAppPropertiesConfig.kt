package com.example.demo.config

import com.fasterxml.jackson.databind.ObjectMapper
import mu.KotlinLogging
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@EnableConfigurationProperties(value = [SampleProperties::class])
class SampleAppPropertiesConfig(
    val properties: SampleProperties
) {

    private val logger = KotlinLogging.logger {}

    @Bean
    fun dummyBean(): ObjectMapper = with(properties) {
        logger.info { "Name: ${properties.name}; ID: ${properties.id}" }
        ObjectMapper()
    }
}

@ConstructorBinding
@ConfigurationProperties(prefix = "sample")
data class SampleProperties(
    val name: String,
    val id: String
)
