import io.syscall.gradle.conventions.CustomJavaExtension
import org.jetbrains.kotlin.gradle.tasks.CompileUsingKotlinDaemon
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

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

val customJavaExt = extensions.getByName<CustomJavaExtension>("customJava")

val DEFAULT_JAVA_VERSION: JavaVersion by project.ext


kotlin {
    jvmToolchain(DEFAULT_JAVA_VERSION.majorVersion.toInt())
    explicitApi()
}

// Kapt fails on JDK 21
// kaptGenerateStubsKotlin FAILED
// java.lang.IllegalAccessError: superclass access check failed: class org.jetbrains.kotlin.kapt3.base.javac.KaptJavaCompiler (in unnamed module @<...>) cannot access class com.sun.tools.javac.main.JavaCompiler (in module jdk.compiler) because module jdk.compiler does not export com.sun.tools.javac.main to unnamed module @<...>
tasks.withType<CompileUsingKotlinDaemon>().configureEach {
    if (name.contains("kapt")) {
        kotlinDaemonJvmArguments.addAll("--add-opens=jdk.compiler/com.sun.tools.javac.main=ALL-UNNAMED")
    }
}

tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions.jvmTarget = DEFAULT_JAVA_VERSION.majorVersion
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
