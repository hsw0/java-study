plugins {
    id("conventions.project.kotlin")
    id("conventions.project.spring-boot-app")
}

group = "io.syscall.hsw.study"
version = "1.0-SNAPSHOT"

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("com.google.guava:guava")
}
