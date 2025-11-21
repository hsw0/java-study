plugins {
    id("conventions.base")
    id("conventions.java")
    id("conventions.project.spotless")
    `java-library`
}

group = "io.syscall.util"
version = "1.0-SNAPSHOT"

tasks.withType<JavaCompile>().configureEach {
    options.release = 17
}

dependencies {
    compileOnlyApi("jakarta.annotation:jakarta.annotation-api:3.0.0")
}
