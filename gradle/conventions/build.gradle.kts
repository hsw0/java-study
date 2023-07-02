plugins {
    `kotlin-dsl`
    alias(libs.plugins.spotless)
}

repositories {
    gradlePluginPortal()
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
