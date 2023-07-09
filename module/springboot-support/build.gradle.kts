plugins {
    id("conventions.project.java")
    id("conventions.project.spring-boot")
    `java-library`
}

group = "dummy"

dependencies {
    compileOnly(project(":module:annotations"))

    implementation("org.springframework.boot:spring-boot:[3.0,)")
    implementation("org.springframework.boot:spring-boot-autoconfigure:[3.0,)")
}
