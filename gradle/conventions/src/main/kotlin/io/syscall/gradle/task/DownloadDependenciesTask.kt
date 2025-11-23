package io.syscall.gradle.task

import org.gradle.api.DefaultTask
import org.gradle.api.artifacts.ResolvableDependencies
import org.gradle.api.artifacts.component.ProjectComponentIdentifier
import org.gradle.api.tasks.TaskAction

abstract class DownloadDependenciesTask : DefaultTask() {
    private val projectName = project.name
    private val allConfigurations: Map<String, ResolvableDependencies>

    init {
        group = "build setup"
        description = "Download all dependencies into the cache"
        notCompatibleWithConfigurationCache("Unsupported")

        val configurations =
            project.configurations.matching { it.isCanBeResolved }.associate { it.name to it.incoming }
        val buildConfigurations =
            project.buildscript.configurations
                .matching { it.isCanBeResolved }
                .associate { "build-${it.name}" to it.incoming }
        allConfigurations = configurations + buildConfigurations
    }

    @TaskAction
    fun action() {
        for ((name, configuration) in allConfigurations.entries) {
            logger.lifecycle("Resolving project: {} configuration: {}", projectName, name)
            val artifacts =
                configuration
                    .artifactView {
                        lenient(true)
                        componentFilter { it !is ProjectComponentIdentifier }
                    }.artifacts
            for (it in artifacts) {
                logger.info(" Resolved artifact: {} => {}", it.id, it.file)
            }
        }
    }
}
