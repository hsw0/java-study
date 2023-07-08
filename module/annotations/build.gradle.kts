plugins {
    id("conventions.base")
    id("conventions.java")
    id("conventions.project.spotless")
    `java-library`
}

group = "io.syscall.util"
version = "1.0-SNAPSHOT"

tasks.withType<JavaCompile>().configureEach {
    options.release.set(11)
}

dependencies {
    compileOnly("com.google.code.findbugs:jsr305:3.0.2")
    compileOnly("jakarta.annotation:jakarta.annotation-api:2.1.1")
    compileOnly("io.github.eisop:checker-qual:3.34.0-eisop1")
}
