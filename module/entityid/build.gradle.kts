plugins {
    id("conventions.project.java")
    id("conventions.project.kotlin")
    id("conventions.publishing")
    `java-library`
    `java-test-fixtures`
}

group = "io.syscall.commons"
version = "1.0-SNAPSHOT"


dependencies {
    compileOnly(project(":module:annotations"))
    testRuntimeOnly(testFixtures(project(":module:logging-support")))

    implementation(kotlin("reflect"))

    testImplementation("io.github.oshai:kotlin-logging-jvm")
}
