plugins {
    id("conventions.project.kotlin")
    id("conventions.project.spring-boot-app")
}

group = "io.syscall.hsw.study"
version = "1.0-SNAPSHOT"

dependencies {
    implementation(project(":dummy-library"))
}
