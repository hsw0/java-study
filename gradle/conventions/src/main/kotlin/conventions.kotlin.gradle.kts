import io.syscall.gradle.conventions.CustomJavaExtension
import io.syscall.gradle.conventions.CustomJvmExtension
import org.jetbrains.kotlin.gradle.internal.KaptWithoutKotlincTask
import org.jetbrains.kotlin.gradle.tasks.CompileUsingKotlinDaemon
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jetbrains.kotlin.gradle.tasks.UsesKotlinJavaToolchain

/**
 * Kotlin convention
 *
 */
interface Comments


plugins {
    id("conventions.project.base")
    id("conventions.java")
    kotlin("jvm")
}

val customJavaExt = extensions.getByType<CustomJavaExtension>()
val customJvmExt = extensions.getByType<CustomJvmExtension>()

kotlin {
    jvmToolchain(customJvmExt.kotlinToolchain.get().asInt())

    explicitApi()
}

java {
    // Overwritten by kotlin plugin
    // > Note that setting a toolchain via the kotlin extension updates the toolchain for Java compile tasks as well.
    toolchain.languageVersion.set(customJvmExt.javaToolchain.get())
}

val service = project.extensions.getByType<JavaToolchainService>()
val customLauncher = service.launcherFor {
    languageVersion.set(customJvmExt.kotlinToolchain)
}
tasks.withType<UsesKotlinJavaToolchain>().configureEach {
    kotlinJavaToolchain.toolchain.use(customLauncher)
}
tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions.jvmTarget = customJvmExt.kotlinJvmTarget.get().toString()
}

// Kapt fails on JDK 21
// kaptGenerateStubsKotlin FAILED
// java.lang.IllegalAccessError: superclass access check failed: class org.jetbrains.kotlin.kapt3.base.javac.KaptJavaCompiler (in unnamed module @<...>) cannot access class com.sun.tools.javac.main.JavaCompiler (in module jdk.compiler) because module jdk.compiler does not export com.sun.tools.javac.main to unnamed module @<...>
tasks.withType<CompileUsingKotlinDaemon>().configureEach {
    kotlinDaemonJvmArguments.add("--add-opens=jdk.compiler/com.sun.tools.javac.main=ALL-UNNAMED")
}
tasks.withType<KaptWithoutKotlincTask>().configureEach {
    kaptProcessJvmArgs.add("--add-opens=jdk.compiler/com.sun.tools.javac.main=ALL-UNNAMED")
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

dependencies {
    implementation(kotlin("stdlib"))
}
