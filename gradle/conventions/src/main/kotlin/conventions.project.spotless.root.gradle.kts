import com.diffplug.gradle.spotless.SpotlessExtension
import com.diffplug.gradle.spotless.SpotlessExtensionPredeclare
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

if (project != rootProject) {
    throw UnsupportedOperationException("Root project only!")
}

configure<SpotlessExtension> {
    predeclareDeps()
}

configure<SpotlessExtensionPredeclare> {
    java {
        palantirJavaFormat(versionCatalog.versions["palantirJavaFormat"].toString())
    }
    kotlin {
        ktlint()
    }
    kotlinGradle {
        ktlint()
    }
}
