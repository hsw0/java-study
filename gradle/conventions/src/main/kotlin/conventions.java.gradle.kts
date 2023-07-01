import org.gradle.api.internal.tasks.compile.HasCompileOptions
import org.gradle.api.tasks.testing.logging.TestExceptionFormat

/**
 * Gradle java plugin config
 *
 * @see org.gradle.api.plugins.JavaPlugin
 */
interface Comments

plugins {
    id("conventions.java-base")
    java
}

val DEFAULT_JAVA_VERSION: JavaVersion by project.ext

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(DEFAULT_JAVA_VERSION.majorVersion))
    }

    disableAutoTargetJvm()
}

tasks.withType<AbstractCompile>().configureEach {
    if (this is HasCompileOptions) {
        options.encoding = "UTF-8"
    }
}

tasks.withType<JavaCompile>().configureEach {
    with(options) {
        release.set(DEFAULT_JAVA_VERSION.majorVersion.toInt())

        compilerArgs.add("-parameters")
    }
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
    testLogging {
        exceptionFormat = TestExceptionFormat.FULL
    }
}

testing {
    @Suppress("UnstableApiUsage")
    suites.withType<JvmTestSuite>().configureEach {
        dependencies {
            implementation("org.junit.jupiter:junit-jupiter-api")
            implementation("org.junit.jupiter:junit-jupiter-params")
            runtimeOnly("org.junit.jupiter:junit-jupiter-engine")
        }
    }
}
