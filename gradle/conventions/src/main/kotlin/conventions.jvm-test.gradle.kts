@file:Suppress("UnstableApiUsage")

import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent

/**
 * 각종 테스트 설정
 *
 * @see org.gradle.api.plugins.JvmTestSuitePlugin
 */
interface Comments

plugins {
    java
}

val versionCatalog = extensions.getByType<VersionCatalogsExtension>().named("libs")

testing {
    val jUnitVersion = versionCatalog.findVersion("junit").get().preferredVersion

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
