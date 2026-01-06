import dev.detekt.gradle.Detekt
import dev.detekt.gradle.extensions.DetektExtension
import io.syscall.gradle.conventions.kotlin

/**
 * Kotlin static analyzers
 *
 * * [Detekt](https://detekt.dev/)
 */
private object Comments

plugins {
    id("conventions.dependency-management")
    kotlin("jvm")

    id("dev.detekt")
}

configure<DetektExtension> {
    // Default: src/{main,test}/{java,kotlin}
    // src/{main,test}/kotlin
    val allKotlinSourceDirs =
        project.sourceSets.map {
            it.extensions["kotlin"] as SourceDirectorySet
        }
    source.setFrom(allKotlinSourceDirs)
}

tasks.withType<Detekt>().configureEach {
    jvmTarget =
        kotlin.compilerOptions.jvmTarget
            .get()
            .target
}
