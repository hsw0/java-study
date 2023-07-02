plugins {
    id("conventions.project.kotlin")
    id("conventions.project.spring-boot-app")
    id("io.syscall.gradle.plugin.mapstruct")
}

group = "io.syscall.hsw.study"
version = "1.0-SNAPSHOT"

dependencies {
    implementation(project(":dummy-library"))
}

mapstruct {
    verbose.set(true)
}
