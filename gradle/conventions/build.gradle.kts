import org.jetbrains.kotlin.gradle.dsl.JvmTarget as KotlinGradlePluginJvmTarget

plugins {
    `kotlin-dsl`
    alias(libs.plugins.spotless)
}

/*
Kotlin plugin fails on JDK > 19
* https://github.com/gradle/gradle/issues/23488
* https://youtrack.jetbrains.com/issue/KT-57495/Add-JVM-target-bytecode-version-20

Execution failed for task ':conventions:compileKotlin'.
> Error while evaluating property 'compilerOptions.jvmTarget' of task ':conventions:compileKotlin'.
  > Failed to calculate the value of property 'jvmTarget'.
     > Unknown Kotlin JVM target: 21
 */
try {
    KotlinGradlePluginJvmTarget.fromTarget(JavaVersion.current().toString())
} catch (e: IllegalArgumentException) {
    logger.warn("Kotlin gradle plugin doesn't supports current jvm: '${e.message}'. Applying workaround")
    // https://docs.gradle.org/8.2/userguide/kotlin_dsl.html#ex-changing-the-jvm-target-using-toolchains
    java.toolchain.languageVersion.convention(JavaLanguageVersion.of(17))

    // kotlin.jvmToolchain(17) // p.s. 이렇게만 해도 됨
}

dependencies {
    implementation(gradleApi())

    implementation(libs.gradlePlugin.kotlin)
    implementation(libs.gradlePlugin.kotlin.allopen)
    implementation(libs.gradlePlugin.kotlin.noarg)

    implementation(libs.gradlePlugin.spotless)
    implementation(libs.gradlePlugin.detekt)
    implementation(libs.gradlePlugin.errorprone)
    implementation(libs.gradlePlugin.checkerframework)

    implementation(libs.gradlePlugin.springBoot)
}

spotless {
    lineEndings = com.diffplug.spotless.LineEnding.UNIX

    kotlin {
        target("src/main/kotlin/**/*.kt")
        ktlint()
    }
    kotlinGradle {
        ktlint()
    }
}

gradlePlugin {
    plugins {
        create("MapStructPlugin") {
            id = "io.syscall.gradle.plugin.mapstruct"
            implementationClass = "io.syscall.gradle.plugin.mapstruct.MapStructPlugin"
        }
    }

    plugins {
        create("DevelopmentOnlyPlugin") {
            id = "io.syscall.gradle.plugin.devonly"
            implementationClass = "io.syscall.gradle.plugin.devonly.DevelopmentOnlyPlugin"
        }
    }
}

// Intellij
tasks.withType<Task>().configureEach {
    if (name in setOf("DownloadSources", "DependenciesReport")) {
        notCompatibleWithConfigurationCache("Incompatible")
    }
}
