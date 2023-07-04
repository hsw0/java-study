@file:Suppress("UnstableApiUsage")

import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent

/**
 * 각종 테스트 설정
 *
 * @see org.gradle.api.plugins.JvmTestSuitePlugin
 * @see <a href="https://docs.gradle.org/current/userguide/jvm_test_suite_plugin.html">JVM Test Suite Plugin</a>
 * @see <a href="https://docs.gradle.org/current/userguide/jacoco_plugin.html">JaCoCo Plugin</a>
 */
interface Comments

plugins {
    java
    jacoco
}

val versionCatalog = extensions.getByType<VersionCatalogsExtension>().named("libs")

jacoco {
    toolVersion = versionCatalog.findVersion("jacoco").get().toString()
}

afterEvaluate {
    testing {
        val jUnitVersion = versionCatalog.findVersion("junit").get().toString()

        suites.withType<JvmTestSuite>().configureEach {
            // Test framework 및 junit-jupiter 의존성 추가
            useJUnitJupiter(jUnitVersion)
        }
    }
}

tasks.withType<Test>().configureEach {
    // Test framework만 설정
    useJUnitPlatform()
    testLogging {
        events(TestLogEvent.PASSED, TestLogEvent.SKIPPED, TestLogEvent.FAILED)
        showStandardStreams = true
        exceptionFormat = TestExceptionFormat.FULL
    }
}
