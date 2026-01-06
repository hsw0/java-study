import org.jetbrains.kotlin.gradle.dsl.JvmDefaultMode
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
        jvmTarget.convention(JvmTarget.JVM_25)
        languageVersion.convention(KotlinVersion.KOTLIN_2_3)
        apiVersion.convention(KotlinVersion.KOTLIN_2_3)

        // https://kotlinlang.org/docs/whatsnew22.html#changes-to-default-method-generation-for-interface-functions
        jvmDefault = JvmDefaultMode.NO_COMPATIBILITY
    }
}

// endregion

configure<KotlinProjectExtension> {
    explicitApi()
}

tasks.withType<KotlinCompile>().configureEach {
    compilerOptions.javaParameters.convention(true)
    compilerOptions.freeCompilerArgs.convention(listOf("-Xjsr305=strict"))
}

val implementationDependencies =
    listOf(
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
