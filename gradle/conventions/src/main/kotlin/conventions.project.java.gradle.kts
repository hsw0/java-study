import net.ltgt.gradle.errorprone.errorprone

/**
 * Java project convention
 *
 */
interface Comments

plugins {
    id("conventions.project.jvm")
    id("conventions.project.spotless")
    id("net.ltgt.errorprone")
}

dependencies {
    add("errorprone", "com.google.errorprone:error_prone_core")
}

tasks.withType<JavaCompile>().configureEach {
    options.errorprone {
        disableWarningsInGeneratedCode.set(true)
        allDisabledChecksAsWarnings.set(true)
    }
}
