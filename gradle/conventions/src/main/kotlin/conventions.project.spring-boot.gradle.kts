import org.jetbrains.kotlin.gradle.plugin.KaptExtension

/**
 * Spring Boot 에 의존하는 project convention
 *
 */
interface Comments

plugins {
    id("conventions.project.jvm")
}

if (pluginManager.hasPlugin("org.jetbrains.kotlin.jvm")) {
    logger.info("Spring Boot: Applying kotlin specific config")

    apply(plugin = "org.jetbrains.kotlin.plugin.spring")
    apply(plugin = "org.jetbrains.kotlin.kapt")

    configure<KaptExtension> {
        showProcessorStats = true
    }
}

val aptConfiguration = if (configurations.get("kapt") != null) {
    "kapt"
} else {
    "annotationProcessor"
}

dependencies {
    implementation(platform("org.springframework.boot:spring-boot-dependencies"))

    add(aptConfiguration, "org.springframework:spring-context-indexer")
    add(aptConfiguration, "org.springframework.boot:spring-boot-configuration-processor")
    add(aptConfiguration, "org.springframework.boot:spring-boot-autoconfigure-processor")

    compileOnly("org.springframework:spring-core")
    compileOnly("org.springframework:spring-context")
    compileOnly("org.springframework:spring-beans")
    compileOnly("org.springframework.boot:spring-boot-autoconfigure")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
}
