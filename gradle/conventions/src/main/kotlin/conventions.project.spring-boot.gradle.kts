import org.jetbrains.kotlin.allopen.gradle.SpringGradleSubplugin
import org.jetbrains.kotlin.gradle.internal.Kapt3GradleSubplugin.Companion.getKaptConfigurationName

/**
 * Spring Boot 에 의존하는 project convention
 *
 */
private object Comments

plugins {
    id("conventions.project.jvm")
    id("conventions.dependency-management.spring-boot")
}

if (pluginManager.hasPlugin("org.jetbrains.kotlin.jvm")) {
    logger.info("Spring Boot: Applying kotlin specific config")

    apply(plugin = "org.jetbrains.kotlin.plugin.allopen")
    apply(plugin = "org.jetbrains.kotlin.kapt")

    apply<SpringGradleSubplugin>() // allopen
}

val useKotlinApt = pluginManager.hasPlugin("org.jetbrains.kotlin.kapt")
val applyAptToSourceSets = listOf("main", "test")

dependencies {
    //implementation(platform("org.springframework.boot:spring-boot-dependencies"))

    val aptConfigurations: List<String> = if (useKotlinApt) {
        applyAptToSourceSets.map(::getKaptConfigurationName)
    } else {
        applyAptToSourceSets.map { sourceSets[it].annotationProcessorConfigurationName }
    }
    for (configurationName in aptConfigurations) {
        add(configurationName, "org.springframework:spring-context-indexer")
        add(configurationName, "org.springframework.boot:spring-boot-configuration-processor")
        add(configurationName, "org.springframework.boot:spring-boot-autoconfigure-processor")
    }

    compileOnly("org.springframework:spring-core")
    compileOnly("org.springframework:spring-context")
    compileOnly("org.springframework:spring-beans")
    compileOnly("org.springframework.boot:spring-boot-autoconfigure")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
}
