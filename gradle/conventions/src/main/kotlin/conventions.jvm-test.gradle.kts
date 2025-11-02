@file:Suppress("UnstableApiUsage") // JvmTestSuite is @Incubating

import io.syscall.gradle.conventions.versionCatalog
import io.syscall.gradle.conventions.versions
import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent

/**
 * 각종 테스트 설정
 *
 * @see org.gradle.api.plugins.JvmTestSuitePlugin
 * @see <a href="https://docs.gradle.org/current/userguide/jvm_test_suite_plugin.html">JVM Test Suite Plugin</a>
 * @see <a href="https://docs.gradle.org/current/userguide/jacoco_plugin.html">JaCoCo Plugin</a>
 */

plugins {
    java
    jacoco
}

val jacocoVersion = versionCatalog.versions["jacoco"].toString()
val jUnitVersion = versionCatalog.versions["junit"].toString()

jacoco {
    toolVersion = jacocoVersion
}

testing.suites.withType<JvmTestSuite>().configureEach {
    useJUnitJupiter(jUnitVersion)
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
