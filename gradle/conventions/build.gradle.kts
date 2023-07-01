
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
    implementation(libs.gradlePlugin.spotless)
    implementation(libs.gradlePlugin.kotlin)
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
