plugins {
    id("conventions.project.kotlin")
    id("conventions.project.spring-boot-app")
    id("io.syscall.gradle.plugin.mapstruct")
    id("io.syscall.gradle.plugin.devonly")
}

group = "io.syscall.hsw.study"
version = "1.0-SNAPSHOT"

dependencies {
    implementation(project(":dummy-library"))
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    devRuntimeOnly("org.springframework.boot:spring-boot-devtools")
}

mapstruct {
    verbose.set(true)
}
