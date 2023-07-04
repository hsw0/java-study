import io.syscall.gradle.conventions.CustomJvmExtension
import io.syscall.gradle.conventions.UnconfiguredVersion
import org.checkerframework.gradle.plugin.CheckerFrameworkExtension

plugins {
    id("conventions.project.java")
    id("conventions.publishing")
    `java-library`
    alias(libs.plugins.mrjar)
}

group = "io.syscall.hsw.study"
version = "1.0-SNAPSHOT"

configure<CustomJvmExtension> {
    javaToolchain.set(UnconfiguredVersion)
    javaTarget.set(UnconfiguredVersion)
}

multiRelease {
    targetVersions(17, 21)
}

configure<CheckerFrameworkExtension> {
    skipCheckerFramework = true
}

dependencies {

}
