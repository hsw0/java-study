
plugins {
    id("conventions.project.kotlin")
    id("conventions.publishing")
    `java-library`
    id("io.syscall.gradle.plugin.mapstruct")
}

group = "io.syscall.hsw.study"
version = "1.0-SNAPSHOT"

dependencies {
}

mapstruct {
    defaultComponentModel.set("jakarta")
}
