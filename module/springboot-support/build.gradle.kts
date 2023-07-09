plugins {
    id("conventions.project.java")
    id("conventions.project.spring-boot")
    `java-library`
}

group = "dummy"

dependencies {
    compileOnly(project(":module:annotations"))

    implementation("org.springframework.boot:spring-boot")
    implementation("org.springframework.boot:spring-boot-autoconfigure")
}
