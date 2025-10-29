import com.diffplug.gradle.spotless.SpotlessExtension
import com.diffplug.gradle.spotless.SpotlessExtensionPredeclare

/**
 * Spotless
 *
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
        palantirJavaFormat("2.81.0")
    }
    kotlin {
        ktlint()
    }
    kotlinGradle {
        ktlint()
    }
}
