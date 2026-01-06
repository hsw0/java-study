plugins {
    id("conventions.project.java")
    id("conventions.project.java-analysis")
    id("conventions.project.spring-boot")
    `java-library`
}

group = "dummy"

// Empty placeholder module for future API-related utilities
// WebFlux error handlers have been moved to :module:springboot-app-base (webflux variant)
dependencies {
    implementation(project(":module:protocol"))
    implementation(project(":module:springboot-app-base"))
}
