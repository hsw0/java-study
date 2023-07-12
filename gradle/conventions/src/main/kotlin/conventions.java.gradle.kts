import io.syscall.gradle.conventions.CustomJavaExtension
import org.gradle.api.internal.tasks.compile.HasCompileOptions
import java.util.jar.Attributes.Name as JarAttribute

/**
 * Gradle java plugin config
 *
 * @see org.gradle.api.plugins.JavaPlugin
 */
private object Comments

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

val defaultJvmTarget = JavaLanguageVersion.of(17)

java {
    toolchain.languageVersion.convention(defaultJvmTarget)
    disableAutoTargetJvm()
}

tasks.withType<JavaCompile>().configureEach {
    options.release.convention(defaultJvmTarget.asInt())
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

afterEvaluate {
    val configuredToolchainVersion = java.toolchain.languageVersion.orNull ?: return@afterEvaluate

    val javaToolchains = project.extensions.getByType<JavaToolchainService>()
    val launcher = javaToolchains.launcherFor {
        languageVersion.set(JavaLanguageVersion.of(21))
    }

    tasks.withType<JavaExec>().configureEach {
        logger.info("${this.path}: Using toolchain version $configuredToolchainVersion")
        this.javaLauncher.set(launcher)
    }
}
