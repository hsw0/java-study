plugins {
    id("conventions.project.kotlin")
    id("conventions.project.spring-boot-app")
}

group = "io.syscall.hsw.study"

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-webflux")
}
