package io.syscall.gradle.plugin.mapstruct

import io.syscall.gradle.plugin.mapstruct.dsl.MapStructExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.tasks.SourceSet
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.get

class MapStructPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        project.pluginManager.apply(JavaPlugin::class.java)

        val extension = project.extensions.create(
            MapStructExtension::class.java,
            "mapstruct",
            DefaultMapStructExtension::class.java,
        ) as DefaultMapStructExtension

        addDependencies(project, extension)
        addJavaCompileOptions(project, extension)
    }

    private fun addDependencies(project: Project, extension: DefaultMapStructExtension) {
        val sourceSets = project.extensions.getByType(SourceSetContainer::class.java)
        val mainSourceSet = sourceSets[SourceSet.MAIN_SOURCE_SET_NAME]

        val version: String = extension.version.get()

        with(project.dependencies) {
            val mapstructDep = create(group = "org.mapstruct", name = "mapstruct", version = version)
            val mapstructProcessorDep = create(group = "org.mapstruct", name = "mapstruct-processor", version = version)
            val jakartaInjectDep = create(group = "jakarta.inject", name = "jakarta.inject-api", version = "2.0.1")

            add(mainSourceSet.compileOnlyConfigurationName, mapstructDep)
            add(mainSourceSet.annotationProcessorConfigurationName, mapstructProcessorDep)
            add(mainSourceSet.compileOnlyConfigurationName, jakartaInjectDep)
        }
    }

    private fun addJavaCompileOptions(project: Project, extension: DefaultMapStructExtension) {
        project.tasks.withType(JavaCompile::class.java).configureEach {
            for (option in extension.supportedOptions) {
                options.compilerArgs.add("-Amapstruct.${option.key}=" + option.value.call())
            }
        }
    }
}

internal abstract class DefaultMapStructExtension : MapStructExtension {
    internal val supportedOptions = mapOf(
        "verbose" to verbose::get,
        "defaultComponentModel" to defaultComponentModel::get,
        "unmappedTargetPolicy" to unmappedTargetPolicy::get,
        "suppressGeneratorTimestamp" to suppressGeneratorTimestamp::get,
    )

    init {
        version.convention("1.5.5.Final")

        verbose.convention(true)
        defaultComponentModel.convention("default")
        unmappedTargetPolicy.convention("ERROR")
        suppressGeneratorTimestamp.convention(true)
    }
}
