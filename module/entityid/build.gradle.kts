plugins {
    id("conventions.project.java")
    id("conventions.project.kotlin")
}

group = "io.syscall.util"
version = "1.0-SNAPSHOT"

dependencies {
    compileOnly(project(":module:annotations"))

    implementation("org.hibernate.orm:hibernate-core")
    implementation(kotlin("stdlib"))
    implementation(kotlin("reflect"))

    testImplementation("io.github.oshai:kotlin-logging-jvm")
}
