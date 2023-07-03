plugins {
    `kotlin-dsl`
    alias(libs.plugins.spotless)
}

repositories {
    gradlePluginPortal()
}

// Kotlin plugin fails on JDK 21
// Execution failed for task ':conventions:compileKotlin'.
//> Error while evaluating property 'compilerOptions.jvmTarget' of task ':conventions:compileKotlin'.
//   > Failed to calculate the value of property 'jvmTarget'.
//      > Unknown Kotlin JVM target: 21
java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

dependencies {
    implementation(gradleApi())

    implementation(platform(libs.bom.kotlin))
    implementation(libs.gradlePlugin.kotlin)
    implementation(libs.gradlePlugin.kotlin.allopen)
    implementation(libs.gradlePlugin.kotlin.noarg)

    implementation(libs.gradlePlugin.spotless)
    implementation(libs.gradlePlugin.errorprone)
    implementation(libs.gradlePlugin.checkerframework)

    implementation(libs.gradlePlugin.springBoot)
}

spotless {
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
