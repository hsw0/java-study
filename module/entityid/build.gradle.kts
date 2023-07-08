
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinVersion

plugins {
    id("conventions.project.java")
    id("conventions.project.kotlin")
    id("conventions.publishing")
    `java-library`
    `java-test-fixtures`
}

group = "io.syscall.commons"
version = "1.0-SNAPSHOT"

kotlin {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_11)
        languageVersion.set(KotlinVersion.KOTLIN_1_7)
    }
}

tasks.withType<JavaCompile>().configureEach {
    options.release.set(11)
}

dependencies {
    compileOnly(project(":module:annotations"))

    implementation(kotlin("reflect"))

    testImplementation("io.github.oshai:kotlin-logging-jvm")
}
