import com.diffplug.gradle.spotless.SpotlessExtension
import com.diffplug.gradle.spotless.SpotlessExtensionPredeclare
import org.gradle.kotlin.dsl.configure

/**
 * Spotless
 *
 * Based on [otel.spotless-conventions](https://github.com/open-telemetry/opentelemetry-java-instrumentation/blob/bba8a073901f6ffd3e415e2570efda075656468d/conventions/src/main/kotlin/otel.spotless-conventions.gradle.kts)
 */
private object Comments

plugins {
    id("com.diffplug.spotless")
}

if (project != rootProject) {
    throw UnsupportedOperationException("Root project only!")
}

configure<SpotlessExtension> {
    predeclareDeps()
}

configure<SpotlessExtensionPredeclare> {
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
