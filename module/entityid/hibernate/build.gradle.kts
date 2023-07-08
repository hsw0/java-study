import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("conventions.project.java")
    id("conventions.project.kotlin")
}

group = "io.syscall.util"
version = "1.0-SNAPSHOT"


tasks.withType<JavaCompile>().configureEach {
    options.release.set(17)
}

tasks.withType<KotlinCompile>().configureEach {
    compilerOptions.jvmTarget.set(JvmTarget.JVM_17)
}


dependencies {
    compileOnly(project(":module:annotations"))
    api(project(":module:entityid"))

    implementation("org.hibernate.orm:hibernate-core")

    testImplementation("io.github.oshai:kotlin-logging-jvm")
}
