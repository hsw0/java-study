plugins {
    id("conventions.project.java")
    id("conventions.project.kotlin")
    `java-test-fixtures`
}

group = "io.syscall.util"
version = "1.0-SNAPSHOT"


dependencies {
    compileOnly(project(":module:annotations"))
    implementation(kotlin("stdlib"))
    implementation(kotlin("reflect"))

    testImplementation("io.github.oshai:kotlin-logging-jvm")
    testFixturesImplementation(kotlin("stdlib"))
    testFixturesImplementation(kotlin("reflect"))
}
