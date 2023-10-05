import com.diffplug.gradle.spotless.SpotlessExtension
import com.diffplug.spotless.LineEnding

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
            palantirJavaFormat("2.38.0")

            toggleOffOn()
        }
    }

    if (pluginManager.hasPlugin("org.jetbrains.kotlin.jvm")) {
        kotlin {
            with(ktlint()) {
                editorConfigOverride(mapOf())
            }
        }

    }
}
