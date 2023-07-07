plugins {
    id("conventions.project.java")
    id("conventions.project.kotlin")
}

group = "io.syscall.util"
version = "1.0-SNAPSHOT"

dependencies {
    compileOnly(project(":module:annotations"))
    api(project(":module:entityid"))

    implementation("org.hibernate.orm:hibernate-core")

    testImplementation("io.github.oshai:kotlin-logging-jvm")
}
