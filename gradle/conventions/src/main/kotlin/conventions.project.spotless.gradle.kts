import com.diffplug.gradle.spotless.SpotlessExtension

/**
 * Spotless
 *
 * Based on [otel.spotless-conventions](https://github.com/open-telemetry/opentelemetry-java-instrumentation/blob/bba8a073901f6ffd3e415e2570efda075656468d/conventions/src/main/kotlin/otel.spotless-conventions.gradle.kts)
 */
private object Comments

plugins {
    id("com.diffplug.spotless")
}

if (project == rootProject) {
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
            if (JavaVersion.current() < JavaVersion.VERSION_21) {
                palantirJavaFormat("2.33.0")
            } else {
                logger.warn("Disabling palantir-java-format on Gradle JVM >= 21")
                // Execution failed for task ':analysis-example:spotlessJava'.
                // > 'com.sun.tools.javac.tree.JCTree com.sun.tools.javac.tree.JCTree$JCImport.getQualifiedIdentifier()'
                // https://github.com/palantir/palantir-java-format/pull/886 아직 발생
            }
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
