import org.jetbrains.kotlin.allopen.gradle.AllOpenExtension
import org.jetbrains.kotlin.gradle.internal.Kapt3GradleSubplugin.Companion.getKaptConfigurationName
import org.jetbrains.kotlin.noarg.gradle.KotlinJpaSubplugin

/**
 * JPA @Entity 클래스를 포함하는 프로젝트
 *
 */
private object Comments

plugins {
    java
}

dependencies {
    implementation("jakarta.persistence:jakarta.persistence-api")
    compileOnly("jakarta.validation:jakarta.validation-api")
}

if (pluginManager.hasPlugin("org.jetbrains.kotlin.jvm")) {
    logger.info("jpa-entity: Applying kotlin specific config")

    apply(plugin = "org.jetbrains.kotlin.plugin.noarg")
    apply(plugin = "org.jetbrains.kotlin.plugin.allopen")
    apply(plugin = "org.jetbrains.kotlin.kapt")

    apply<KotlinJpaSubplugin>() // no-arg preset=jpa
    configure<AllOpenExtension> {
        annotation("jakarta.persistence.Entity")
        annotation("jakarta.persistence.MappedSuperclass")
        annotation("jakarta.persistence.Embeddable")
    }
}

val useKotlinApt = pluginManager.hasPlugin("org.jetbrains.kotlin.kapt")
val applyAptToSourceSets = listOf("main")

dependencies {
    val aptConfigurations: List<String> = if (useKotlinApt) {
        applyAptToSourceSets.map(::getKaptConfigurationName)
    } else {
        applyAptToSourceSets.map { sourceSets[it].annotationProcessorConfigurationName }
    }
    for (configurationName in aptConfigurations) {
        // META-INF/spring.components 에 @Entity, @Table 추가
        // ex) <FQCN of Entity class>=jakarta.persistence.Entity,jakarta.persistence.Table
        add(configurationName, "org.springframework:spring-context-indexer")
    }
}
