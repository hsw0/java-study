/**
 * Spring Boot Application convention
 *
 */
private object Comments

plugins {
    id("conventions.project.spring-boot")
    id("org.springframework.boot")
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.springframework.boot:spring-boot-starter-aop")
    implementation("org.springframework.boot:spring-boot-starter-validation")
}
