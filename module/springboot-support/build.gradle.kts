plugins {
    id("conventions.project.java")
    id("conventions.project.java-analysis")
    id("conventions.project.spring-boot")
    `java-library`
}

dependencies {
    compileOnly(project(":module:annotations"))

    implementation("org.springframework.boot:spring-boot")
    implementation("org.springframework.boot:spring-boot-autoconfigure")
}
