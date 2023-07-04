import com.diffplug.gradle.spotless.SpotlessExtension

/**
 * Spotless
 *
 * Based on [otel.spotless-conventions](https://github.com/open-telemetry/opentelemetry-java-instrumentation/blob/bba8a073901f6ffd3e415e2570efda075656468d/conventions/src/main/kotlin/otel.spotless-conventions.gradle.kts)
 */
interface Comments

plugins {
    id("com.diffplug.spotless")
}

if (project == rootProject) {
    repositories {
        mavenCentral()
    }

    spotless {
        predeclareDeps()
    }

    with(extensions["spotlessPredeclare"] as SpotlessExtension) {
        java {
            palantirJavaFormat("2.33.0")
        }
        kotlin {
            ktlint()
        }
        kotlinGradle {
            ktlint()
        }
    }
}

spotless {
    if (pluginManager.hasPlugin("java")) {
        java {
            palantirJavaFormat("2.33.0")
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
