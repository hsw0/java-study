import com.diffplug.gradle.spotless.SpotlessExtension
import com.diffplug.spotless.LineEnding
import io.syscall.gradle.conventions.versionCatalog
import io.syscall.gradle.conventions.versions

/**
 * Spotless
 *
 */
private object Comments

plugins {
    id("com.diffplug.spotless")
}

configure<SpotlessExtension> {
    lineEndings = LineEnding.UNIX

    if (pluginManager.hasPlugin("java")) {
        java {
            target("src/**/*.java")
            palantirJavaFormat(versionCatalog.versions["palantirJavaFormat"].toString())

            toggleOffOn()
        }
    }

    if (pluginManager.hasPlugin("org.jetbrains.kotlin.jvm")) {
        kotlin {
            with(ktlint()) {
                setEditorConfigPath(project.rootDir.resolve("./.editorconfig"))
                editorConfigOverride(mapOf())
            }
        }
    }
}
