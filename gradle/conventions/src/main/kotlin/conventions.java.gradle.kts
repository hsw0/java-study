
import io.syscall.gradle.conventions.CustomJavaExtension
import org.gradle.api.internal.tasks.compile.HasCompileOptions
import java.util.jar.Attributes.Name as JarAttribute

/**
 * Gradle java plugin config
 *
 * @see org.gradle.api.plugins.JavaPlugin
 */
interface Comments

plugins {
    java
    id("conventions.java-base")
    id("conventions.jvm-test")
}

tasks.withType<AbstractCompile>().configureEach {
    if (this is HasCompileOptions) {
        options.encoding = "UTF-8"
    }
}

val customJavaExt = extensions.create<CustomJavaExtension>("customJava")

java {
    toolchain.languageVersion.convention(JavaLanguageVersion.of(21))
    disableAutoTargetJvm()
}

tasks.withType<JavaCompile>().configureEach {
    options.release.convention(21)
}

tasks.withType<JavaCompile>().configureEach {
    with(options) {
        compilerArgs.add("-parameters")
        compilerArgs.addAll(customJavaExt.buildCompilerArgs())
    }
}

tasks.withType<JavaCompile>().configureEach {
    if (!name.contains("Test")) {
        return@configureEach
    }

    // serialVersionUID is basically guaranteed to be useless in tests
    options.compilerArgs.add("-Xlint:-serial")
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

