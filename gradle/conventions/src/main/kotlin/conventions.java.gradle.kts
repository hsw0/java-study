import io.syscall.gradle.conventions.CustomJavaExtension
import org.gradle.api.internal.tasks.compile.HasCompileOptions
import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent
import java.util.jar.Attributes.Name as JarAttribute

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

val customJavaExt = extensions.create<CustomJavaExtension>("customJava")

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
        compilerArgs.addAll(customJavaExt.buildCompilerArgs())
    }
}

tasks.named<Jar>("jar") {
    manifest {
        attributes(
            JarAttribute.IMPLEMENTATION_TITLE.toString() to project.name,
            JarAttribute.IMPLEMENTATION_VERSION.toString() to project.version,
        )
    }
}

// https://docs.gradle.org/current/userguide/build_cache_concepts.html#normalization
normalization {
    runtimeClasspath {
        metaInf {
            ignoreAttribute("Implementation-Version")
        }
    }
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
    testLogging {
        events(TestLogEvent.PASSED, TestLogEvent.SKIPPED, TestLogEvent.FAILED)
        showStandardStreams = true
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
