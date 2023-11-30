import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinProjectExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinVersion
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

/**
 * Kotlin convention
 *
 */
private object Comments

plugins {
    id("conventions.project.base")
    id("conventions.java")
    kotlin("jvm")
}

// region Toolchain 관련 설정

configure<KotlinJvmProjectExtension> {
    compilerOptions {
        jvmTarget.convention(JvmTarget.JVM_17)
        languageVersion.convention(KotlinVersion.KOTLIN_1_9)
        apiVersion.convention(KotlinVersion.KOTLIN_1_9)
    }
}

// endregion

configure<KotlinProjectExtension> {
    explicitApi()
}

tasks.withType<KotlinCompile>().configureEach {
    compilerOptions.javaParameters.set(true)
    compilerOptions.freeCompilerArgs.addAll("-Xjsr305=strict")
}

val implementationDependencies = listOf(
    dependencies.kotlin("stdlib"),
)

dependencies {
    for (dep in implementationDependencies) {
        implementation(dep)
    }
}

plugins.withType<JavaTestFixturesPlugin> {
    val testFixturesImplementation by configurations

    dependencies {
        for (dep in implementationDependencies) {
            testFixturesImplementation(dep)
        }
    }
}
