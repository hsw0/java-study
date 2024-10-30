package io.syscall.gradle.task

import org.gradle.api.DefaultTask
import org.gradle.api.artifacts.ResolvableDependencies
import org.gradle.api.tasks.TaskAction

abstract class DownloadDependenciesTask : DefaultTask() {
    private val projectName = project.name
    private val configurations: Map<String, ResolvableDependencies>

    init {
        group = "build setup"
        description = "Download all dependencies into the cache"
        notCompatibleWithConfigurationCache("Unsupported")

        configurations = project.configurations.matching { it.isCanBeResolved }.associate { it.name to it.incoming }
    }

    @TaskAction
    fun action() {
        for ((name, configuration) in configurations.entries) {
            logger.lifecycle("Resolving project: {} configuration: {}", projectName, name)
            val artifacts = configuration.artifactView { lenient(true) }.artifacts
            for (it in artifacts) {
                logger.info(" Resolved artifact: {} => {}", it.id, it.file)
            }
        }
    }
}
