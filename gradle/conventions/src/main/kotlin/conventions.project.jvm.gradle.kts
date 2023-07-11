import io.syscall.gradle.conventions.isClasspathLike
import io.syscall.gradle.conventions.isCompileClasspath
import io.syscall.gradle.conventions.isRuntimeClasspath
import io.syscall.gradle.conventions.libs
import io.syscall.gradle.conventions.versionCatalog

/**
 * JVM based project convention
 *
 */
private object Comments

plugins {
    id("conventions.project.base")
    id("conventions.java")
}

dependencies {
    implementation("org.slf4j:slf4j-api:[2.0,)") // Who doesn't?

    // Test framework의 의존성은 아니지만 공통으로 사용
    testImplementation("org.assertj:assertj-core")
}

// region Dependency substitution 적용
// https://docs.gradle.org/8.2/userguide/resolution_rules.html#sec:dependency_substitution_rules

val checkerFrameworkDep = versionCatalog.libs["checkerframework"].toString()
val checkerQualDep = versionCatalog.libs["checkerframework-qual"].toString()

configurations.configureEach {
    if (name == "checkerFramework") {
        resolutionStrategy.dependencySubstitution {
            substitute(module("org.checkerframework:checker"))
                .using(module(checkerFrameworkDep))
        }
    }
    if (isClasspathLike) {
        resolutionStrategy.dependencySubstitution {
            substitute(module("org.checkerframework:checker-qual"))
                .using(module(checkerQualDep))
        }
    }

    if (isCompileClasspath) {
        exclude("org.apache.logging.log4j", "log4j-api")
        exclude("org.apache.logging.log4j", "log4j-core")
        exclude("org.jboss.logging", "jboss-logging")
    } else if (isRuntimeClasspath) {
        resolutionStrategy.dependencySubstitution {
            substitute(module("org.apache.logging.log4j:log4j-api"))
                .using(module("org.slf4j:log4j-over-slf4j:2.+"))
            substitute(module("org.apache.logging.log4j:log4j-to-slf4j"))
                .using(module("org.slf4j:log4j-over-slf4j:2.+"))
        }
    }
}

// endregion
