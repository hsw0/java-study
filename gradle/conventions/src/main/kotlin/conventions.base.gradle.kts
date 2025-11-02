import io.syscall.gradle.task.DownloadDependenciesTask

/**
 * Gradle `base` plugin config
 *
 * @see org.gradle.api.plugins.BasePlugin
 * @see <a href="https://docs.gradle.org/current/userguide/base_plugin.html">Base plugin Doc</a>
 * @see org.gradle.language.base.plugins.LifecycleBasePlugin
 */
private object Comments

// region Reproducible build
tasks.withType<AbstractArchiveTask>().configureEach {
    isPreserveFileTimestamps = false
    isReproducibleFileOrder = true
    filePermissions { unix("rw-r--r--") }
    dirPermissions { unix("rwxr-xr-x") }

    duplicatesStrategy = DuplicatesStrategy.FAIL
}

// endregion

afterEvaluate {
    tasks.withType<AbstractArchiveTask>().configureEach {
        // 프로젝트 구조가 ':mother:child', 'father:child' 가 일 때 child.jar 와 같은 이름 충돌 방지를 위해 full prefix 활용
        archiveBaseName.convention(project.path.replace(":", "-").trimStart('-'))
    }
}

tasks.register<DownloadDependenciesTask>("downloadDependencies")
