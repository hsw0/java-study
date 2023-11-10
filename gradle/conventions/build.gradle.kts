plugins {
    `kotlin-dsl`
    alias(libs.plugins.spotless)
}

dependencies {
    implementation(gradleKotlinDsl())

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
