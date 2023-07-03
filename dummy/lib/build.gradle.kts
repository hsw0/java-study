plugins {
    id("conventions.project.kotlin")
    id("conventions.publishing")
    `java-library`
    id("io.syscall.gradle.plugin.mapstruct")
    id("io.syscall.gradle.plugin.devonly")
}

group = "io.syscall.hsw.study"
version = "1.0-SNAPSHOT"

dependencies {
    devRuntimeOnly("software.amazon.awssdk:arns:2.20.97")

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core-jvm")
    devRuntimeOnly("org.jetbrains.kotlinx:kotlinx-coroutines-debug")
    testRuntimeOnly("org.jetbrains.kotlinx:kotlinx-coroutines-debug")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test")
}

mapstruct {
    defaultComponentModel.set("jakarta")
}
