import io.gitlab.arturbosch.detekt.Detekt
import io.gitlab.arturbosch.detekt.extensions.DetektExtension

/**
 * Kotlin static analyzers
 *
 * * [Detekt](https://detekt.dev/)
 */
private object Comments

plugins {
    id("conventions.dependency-management")
    kotlin("jvm")

    id("io.gitlab.arturbosch.detekt")
}

val Project.sourceSets: SourceSetContainer
    get() = extensions.getByType<SourceSetContainer>()

// src/{main,test}/kotlin
val allKotlinSourceDirs = project.sourceSets.map {
    it.extensions.getByName("kotlin") as SourceDirectorySet
}

configure<DetektExtension> {
    // Default: src/{main,test}/{java,kotlin}
    source.setFrom(allKotlinSourceDirs)
}

tasks.withType<Detekt>().configureEach {
    jvmTarget = kotlin.compilerOptions.jvmTarget.get().target
}
