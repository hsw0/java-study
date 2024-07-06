/**
 * Gradle `base` plugin config
 *
 * @see org.gradle.api.plugins.BasePlugin
 * @see <a href="https://docs.gradle.org/current/userguide/base_plugin.html">Base plugin Doc</a>
 * @see org.gradle.language.base.plugins.LifecycleBasePlugin
 */
private object Comments

// Reproducible build
tasks.withType<AbstractArchiveTask>().configureEach {
    isPreserveFileTimestamps = false
    isReproducibleFileOrder = true
    filePermissions { unix("rw-r--r--") }
    dirPermissions { unix("rwxr-xr-x") }
}

tasks.withType<AbstractArchiveTask>().configureEach {
    duplicatesStrategy = DuplicatesStrategy.FAIL
}
