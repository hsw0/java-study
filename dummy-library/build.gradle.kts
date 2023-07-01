plugins {
    id("conventions.project.kotlin")
    `java-library`
}

group = "io.syscall.hsw.study"
version = "1.0-SNAPSHOT"

customJava {
    javaModuleName.set("io.syscall.hsw.study.dummylib")
}

dependencies {
}
