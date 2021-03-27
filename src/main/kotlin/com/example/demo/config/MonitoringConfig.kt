package com.example.demo.config

import io.micrometer.core.instrument.MeterRegistry
import mu.KotlinLogging
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.net.InetAddress
import java.net.UnknownHostException

@Configuration
class MonitoringConfig {

    private val logger = KotlinLogging.logger {}

    @Bean
    fun metricsCommonTags(): MeterRegistryCustomizer<MeterRegistry> {
        val hostname: String = try {
            InetAddress.getLocalHost().hostName
        } catch (e: UnknownHostException) {
            "unknown"
        }
        logger.info { "Setting servicename: demo and Hostname: $hostname" }
        return MeterRegistryCustomizer { registry ->
            registry.config().commonTags("hostname", hostname, "servicename", "demo", "application", "demo")
        }
    }
}
