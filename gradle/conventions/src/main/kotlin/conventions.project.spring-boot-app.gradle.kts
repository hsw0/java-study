import io.syscall.gradle.conventions.versionCatalog
import io.syscall.gradle.conventions.versions
import org.gradle.api.artifacts.component.ModuleComponentIdentifier
import org.springframework.boot.gradle.tasks.aot.ProcessTestAot
import org.springframework.boot.gradle.tasks.bundling.BootJar

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
    val otelAgentVersion = versionCatalog.versions["opentelemetry-instrumentation"].toString()
    javaAgent("io.opentelemetry.javaagent:opentelemetry-javaagent:$otelAgentVersion")
}

tasks.withType<JavaCompile>().named { it.contains("Aot") }.configureEach {
    options.compilerArgs.removeIf { it.startsWith("-Xlint:") }
    options.compilerArgs.add("-Xlint:none")
}

tasks.withType<ProcessTestAot>().configureEach {
    enabled = false
}

// Embed OpenTelemetry Java agent into the Spring Boot fat jar
tasks.named<BootJar>("bootJar") {
    // Copy the agent JAR file into the javaagent/ directory in the fat jar
    // To make configuration cache compatible - pre-compute rename mapping
    val renameMap: Provider<Map<String, String>> =
        javaAgent.incoming.artifacts.resolvedArtifacts.map { artifacts ->
            artifacts.associate { artifact ->
                val originalName = artifact.file.name
                val componentId = artifact.id.componentIdentifier as? ModuleComponentIdentifier
                val baseName = componentId?.module ?: originalName.substringBeforeLast('.').substringBeforeLast('-')
                originalName to "$baseName.jar"
            }
        }

    from(javaAgent) {
        into("javaagent")
        eachFile {
            renameMap.get()[name]?.let { newName -> name = newName }
        }
    }
}
