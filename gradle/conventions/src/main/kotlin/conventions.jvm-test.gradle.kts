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

testing {
    val jUnitVersion = versionCatalog.findVersion("junit").get().toString()

    suites.withType<JvmTestSuite>().configureEach {
        useJUnitJupiter(jUnitVersion)
    }
}

val integrationTest = testing.suites.create<JvmTestSuite>("integrationTest") {
    dependencies {
        implementation(project())
    }
}

configurations {
    val itSourceSet = sourceSets.get(integrationTest.name)
    val testImplementation by configurations

    named(itSourceSet.implementationConfigurationName) {
        extendsFrom(testImplementation)
    }
}

tasks.withType<Test>().configureEach {
    testLogging {
        events(TestLogEvent.PASSED, TestLogEvent.SKIPPED, TestLogEvent.FAILED)
        showStandardStreams = true
        exceptionFormat = TestExceptionFormat.FULL
    }
}

tasks {
    named<JacocoReport>("jacocoTestReport") {
        dependsOn("test")
    }
}
