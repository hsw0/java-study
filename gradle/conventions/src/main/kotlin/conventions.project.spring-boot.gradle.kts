/**
 * Spring Boot 에 의존하는 project convention
 *
 */
interface Comments

plugins {
    id("conventions.project.java")
}

dependencies {
    implementation(platform("org.springframework.boot:spring-boot-dependencies"))

    annotationProcessor("org.springframework:spring-context-indexer")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
    annotationProcessor("org.springframework.boot:spring-boot-autoconfigure-processor")

    compileOnly("org.springframework:spring-context")
    compileOnly("org.springframework:spring-beans")
    compileOnly("org.springframework.boot:spring-boot-autoconfigure")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
}
