/**
 * Kotlin project convention
 *
 */
private object Comments

plugins {
    id("conventions.project.jvm")
    id("conventions.kotlin")
    id("conventions.project.spotless")
}

@Suppress("UnstableApiUsage")
testing.suites.withType<JvmTestSuite>().configureEach {
    dependencies {
        implementation("org.jetbrains.kotlin:kotlin-test-junit5")

        runtimeOnly("io.kotest:kotest-runner-junit5")
        implementation("io.kotest:kotest-assertions-core")
        implementation("io.kotest:kotest-property")
    }
}
