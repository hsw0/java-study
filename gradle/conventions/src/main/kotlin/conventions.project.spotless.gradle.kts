import com.diffplug.gradle.spotless.SpotlessExtension
import com.diffplug.spotless.LineEnding

/**
 * Spotless
 *
 * Based on [otel.spotless-conventions](https://github.com/open-telemetry/opentelemetry-java-instrumentation/blob/bba8a073901f6ffd3e415e2570efda075656468d/conventions/src/main/kotlin/otel.spotless-conventions.gradle.kts)
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
            palantirJavaFormat("2.35.0")

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
