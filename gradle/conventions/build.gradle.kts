import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    `kotlin-dsl`
    alias(libs.plugins.spotless)
}

// Kotlin plugin fails on JDK 21
// Execution failed for task ':conventions:compileKotlin'.
// > Error while evaluating property 'compilerOptions.jvmTarget' of task ':conventions:compileKotlin'.
//   > Failed to calculate the value of property 'jvmTarget'.
//      > Unknown Kotlin JVM target: 21
try {
    JvmTarget.fromTarget(JavaVersion.current().toString())
} catch (e: IllegalArgumentException) {
    logger.warn("Kotlin gradle plugin doesn't supports current jvm: '${e.message}'. Applying workaround")
    java.toolchain.languageVersion.convention(JavaLanguageVersion.of(17))
}

dependencies {
    implementation(gradleApi())

    implementation(libs.gradlePlugin.kotlin)
    implementation(libs.gradlePlugin.kotlin.allopen)
    implementation(libs.gradlePlugin.kotlin.noarg)

    implementation(libs.gradlePlugin.spotless)
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
