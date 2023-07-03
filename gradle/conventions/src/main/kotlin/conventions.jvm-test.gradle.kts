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

testing {
    val versionCatalog = extensions.getByType<VersionCatalogsExtension>().named("libs")
    val jUnitVersion = versionCatalog.findVersion("junit").get().toString()

    suites.withType<JvmTestSuite>().configureEach {
        useJUnitJupiter(jUnitVersion)
    }
}

tasks.withType<Test>().configureEach {
    testLogging {
        events(TestLogEvent.PASSED, TestLogEvent.SKIPPED, TestLogEvent.FAILED)
        showStandardStreams = true
        exceptionFormat = TestExceptionFormat.FULL
    }
}
