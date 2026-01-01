import io.syscall.gradle.conventions.versionCatalog
import io.syscall.gradle.conventions.versions

/**
 * Spring Boot Application convention
 *
 * Applications using this convention MUST explicitly declare which springboot-app-base variant they need:
 * - For WebFlux apps:
 *     implementation(project(":module:springboot-app-base")) {
 *         capabilities { requireCapability("dummy:springboot-app-base-webflux") }
 *     }
 * - For Servlet apps:
 *     implementation(project(":module:springboot-app-base")) {
 *         capabilities { requireCapability("dummy:springboot-app-base-servlet") }
 *     }
 */
private object Comments

plugins {
    id("conventions.project.spring-boot")
    id("org.springframework.boot")
    id("org.springframework.boot.aot")
}

// Create a custom configuration for the OpenTelemetry Java agent
// This configuration is not on the classpath, just used for bundling
val javaAgent: Configuration by configurations.creating {
    isCanBeConsumed = false
    isCanBeResolved = true
    isTransitive = false
}

dependencies {
    // Applications must explicitly declare which springboot-app-base variant they need
    // See KDoc above for usage examples
    testImplementation(testFixtures(project(":module:springboot-app-base")))

    implementation("org.springframework.boot:spring-boot")
    implementation("org.springframework.boot:spring-boot-autoconfigure")

    developmentOnly("io.projectreactor:reactor-tools")
    developmentOnly("io.projectreactor.tools:blockhound")

    // Add OpenTelemetry Java agent to custom configuration
    val otelAgentVersion = versionCatalog.versions["opentelemetry-javaagent"].toString()
    javaAgent("io.opentelemetry.javaagent:opentelemetry-javaagent:${otelAgentVersion}")
}

tasks.withType<JavaCompile>().named { it.contains("Aot") }.configureEach {
    options.compilerArgs.removeIf { it.startsWith("-Xlint:") }
    options.compilerArgs.add("-Xlint:none")
}

// Embed OpenTelemetry Java agent into the Spring Boot fat jar
tasks.named<org.springframework.boot.gradle.tasks.bundling.BootJar>("bootJar") {
    // Copy the agent JAR file into the javaagent/ directory in the fat jar
    from(javaAgent) {
        into("javaagent")
        rename { "opentelemetry-javaagent.jar" }
    }
}
