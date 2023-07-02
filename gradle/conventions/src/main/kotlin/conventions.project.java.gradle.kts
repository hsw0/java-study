import net.ltgt.gradle.errorprone.errorprone
import org.checkerframework.gradle.plugin.CheckerFrameworkExtension

/**
 * Java project convention
 *
 */
interface Comments

plugins {
    id("conventions.project.jvm")

    id("conventions.project.spotless")
    id("net.ltgt.errorprone")
    id("org.checkerframework")
}

dependencies {
    add("errorprone", "com.google.errorprone:error_prone_core")

    add("checkerFramework", platform(project(":dependencyManagement")))
    add("checkerFramework", "org.checkerframework:checker")
    compileOnly("org.checkerframework:checker-qual")
    testCompileOnly("org.checkerframework:checker-qual")
}

tasks.withType<JavaCompile>().configureEach {
    options.errorprone {
        disableWarningsInGeneratedCode.set(true)
        allDisabledChecksAsWarnings.set(true)
    }
}

configure<CheckerFrameworkExtension> {
    checkers = listOf(
        "org.checkerframework.checker.nullness.NullnessChecker",
        "org.checkerframework.checker.index.IndexChecker",
        "org.checkerframework.checker.tainting.TaintingChecker",
        "org.checkerframework.framework.util.PurityChecker",
    )

    excludeTests = true
}
