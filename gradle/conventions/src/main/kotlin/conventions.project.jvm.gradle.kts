
import io.syscall.gradle.conventions.isCompileClasspath
import io.syscall.gradle.conventions.isRuntimeClasspath

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
    compileOnly(project(":module:annotations"))

    implementation("org.slf4j:slf4j-api") // Who doesn't?

    // 실제 Test fixture는 아니고 테스트 전용 logback-test.xml 설정을 위함
    testRuntimeOnly(testFixtures(project(":module:logging-support")))
}

@Suppress("UnstableApiUsage")
testing.suites.withType<JvmTestSuite>().configureEach {
    dependencies {
        implementation("org.assertj:assertj-core")
    }
}

// region Dependency substitution 적용
// https://docs.gradle.org/8.2/userguide/resolution_rules.html#sec:dependency_substitution_rules

configurations.configureEach {
    if (isCompileClasspath) {
        exclude("org.apache.logging.log4j", "log4j-api")
        exclude("org.apache.logging.log4j", "log4j-core")
        exclude("org.jboss.logging", "jboss-logging")
    } else if (isRuntimeClasspath) {
        resolutionStrategy.dependencySubstitution {
            substitute(module("org.apache.logging.log4j:log4j-core"))
                .using(module("org.apache.logging.log4j:log4j-to-slf4j:2.24.3"))
                .because("Use slf4j")

            substitute(module("log4j:log4j"))
                .using(module("org.slf4j:log4j-over-slf4j:2.0.17"))
                .because("Use slf4j")
        }
    }
}

// endregion
