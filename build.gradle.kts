import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "2.4.4"
    id("io.spring.dependency-management") version "1.0.11.RELEASE"

    kotlin("jvm") version "1.4.31"
    kotlin("plugin.spring") version "1.4.31"

    id("org.jmailen.kotlinter") version "3.4.0"

    application
    id("com.google.cloud.tools.jib") version "2.8.0"
}

group = "com.example"
java.sourceCompatibility = JavaVersion.VERSION_15

application {
    mainClass.set("com.example.demo.DemoApplicationKt")
    applicationDefaultJvmArgs =
        listOf("-server", "-Djava.awt.headless=true", "-XX:+UseShenandoahGC", "-XX:MaxGCPauseMillis=100")
}

version = if (project.hasProperty("version")) {
    project.property("version") ?: "unspecified"
} else {
    "1.0.0-SNAPSHOT"
}

repositories {
    mavenCentral()
}

dependencies {

    // kotlin
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

    // spring
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")

    // k8s
    // implementation("org.springframework.cloud:spring-cloud-starter-kubernetes-client-config")

    // openapi
    implementation("org.springdoc:springdoc-openapi-ui:1.5.6")
    implementation("org.springdoc:springdoc-openapi-kotlin:1.5.6")

    // logging
    implementation("io.github.microutils:kotlin-logging:2.0.6")
    implementation("net.logstash.logback:logstash-logback-encoder:6.6")

    // tests
    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
    }
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict", "-Xjvm-default=all-compatibility")
        jvmTarget = JavaVersion.VERSION_15.toString()
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

// region Jib
tasks["build"].dependsOn(tasks["jib"])

configure<com.google.cloud.tools.jib.gradle.JibExtension> {
    from {
        image = "adoptopenjdk/openjdk15-openj9:alpine-slim"
        auth {
            username = System.getenv("REGISTRY_USERNAME")
            password = System.getenv("REGISTRY_PASSWORD")
        }
    }
    to {
        image = "amaljoyc/${project.name}:${if (version != "unspecified") "$version" else "latest"}"
        auth {
            username = System.getenv("REGISTRY_USERNAME")
            password = System.getenv("REGISTRY_PASSWORD")
        }
    }
    container {
        ports = listOf("8080", "8081")

        jvmFlags = listOf(
            "-Xtune:virtualized",
            "-XX:+IdleTuningGcOnIdle", "-XX:IdleTuningMinFreeHeapOnIdle=10",
            "-XX:+CompactStrings",
            "-XX:+UseContainerSupport", "-XX:MinRAMPercentage=25", "-XX:MaxRAMPercentage=75"
        )
    }
}
// end region Jib
