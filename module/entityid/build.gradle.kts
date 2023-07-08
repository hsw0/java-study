import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinVersion
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("conventions.project.java")
    id("conventions.project.kotlin")
    `java-test-fixtures`
}

group = "io.syscall.util"
version = "1.0-SNAPSHOT"

tasks.withType<JavaCompile>().configureEach {
    options.release.set(11)
}

tasks.withType<KotlinCompile>().configureEach {
    compilerOptions.jvmTarget.set(JvmTarget.JVM_11)
    compilerOptions.languageVersion.set(KotlinVersion.KOTLIN_1_6)
}

dependencies {
    compileOnly(project(":module:annotations"))
    implementation(kotlin("stdlib"))
    implementation(kotlin("reflect"))

    testImplementation("io.github.oshai:kotlin-logging-jvm")
    testFixturesImplementation(kotlin("stdlib"))
    testFixturesImplementation(kotlin("reflect"))
}
