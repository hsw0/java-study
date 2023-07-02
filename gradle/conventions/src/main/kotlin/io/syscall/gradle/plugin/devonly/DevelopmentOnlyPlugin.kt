package io.syscall.gradle.plugin.devonly

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.api.attributes.Bundling
import org.gradle.api.attributes.LibraryElements
import org.gradle.api.attributes.Usage
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.tasks.bundling.AbstractArchiveTask
import org.gradle.kotlin.dsl.named
import org.gradle.kotlin.dsl.withType

/**
 * Spring Boot plugin 의 `developmentOnly` 같은 역할
 *
 * `devRuntimeOnly` Configuration을 등록한다
 *
 * @see org.springframework.boot.gradle.plugin.SpringBootPlugin
 */
class DevelopmentOnlyPlugin : Plugin<Project> {

    companion object {
        const val CONFIGURATION_NAME = "devRuntimeOnly"
        const val PRODUCTION_RUNTIME_CLASSPATH_CONFIGURATION_NAME = "prodRuntimeClasspath"
    }

    override fun apply(project: Project) {
        project.plugins.withType<JavaPlugin> {
            val productionRuntimeClasspath = addProductionRuntimeOnlyConfig(project)
            val developmentOnly = addDevelopmentOnlyConfig(project)

            project.tasks.withType<AbstractArchiveTask>().configureEach {
                val devOnlyClasspath = developmentOnly.minus(productionRuntimeClasspath)

                exclude {
                    devOnlyClasspath.contains(it.file)
                }
            }
        }
    }

    /**
     * @see org.springframework.boot.gradle.plugin.JavaPluginAction.configureDevelopmentOnlyConfiguration
     */
    private fun addDevelopmentOnlyConfig(project: Project): Configuration {
        val runtimeClasspath = project.configurations.getByName(JavaPlugin.RUNTIME_CLASSPATH_CONFIGURATION_NAME)

        val developmentOnly = project.configurations.create(CONFIGURATION_NAME)
        with(developmentOnly) {
            description = "Configuration for development-only dependencies such as Spring Boot's DevTools."
            isVisible = false
            isCanBeResolved = runtimeClasspath.isCanBeResolved
            isCanBeConsumed = runtimeClasspath.isCanBeConsumed
        }

        runtimeClasspath.extendsFrom(developmentOnly)

        return developmentOnly
    }

    private fun addProductionRuntimeOnlyConfig(project: Project): Configuration {
        val runtimeClasspath = project.configurations.getByName(JavaPlugin.RUNTIME_CLASSPATH_CONFIGURATION_NAME)

        val productionRuntimeClasspath = project.configurations.create(PRODUCTION_RUNTIME_CLASSPATH_CONFIGURATION_NAME)
        with(productionRuntimeClasspath.attributes) {
            with(project.objects) {
                attribute(Usage.USAGE_ATTRIBUTE, named<Usage>(Usage.JAVA_RUNTIME))
                attribute(Bundling.BUNDLING_ATTRIBUTE, named<Bundling>(Bundling.EXTERNAL))
                attribute(LibraryElements.LIBRARY_ELEMENTS_ATTRIBUTE, named<LibraryElements>(LibraryElements.JAR))
            }
        }

        with(productionRuntimeClasspath) {
            isVisible = false
            isCanBeConsumed = runtimeClasspath.isCanBeConsumed
            isCanBeResolved = runtimeClasspath.isCanBeResolved

            setExtendsFrom(runtimeClasspath.extendsFrom)
        }

        return productionRuntimeClasspath
    }
}
