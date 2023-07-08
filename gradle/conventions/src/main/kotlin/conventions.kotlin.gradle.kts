import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinProjectExtension
import org.jetbrains.kotlin.gradle.internal.KaptWithoutKotlincTask
import org.jetbrains.kotlin.gradle.tasks.CompileUsingKotlinDaemon
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

/**
 * Kotlin convention
 *
 */
private object Comments


plugins {
    id("conventions.project.base")
    id("conventions.java")
    kotlin("jvm")
}

// region Toolchain 관련 설정

configure<KotlinJvmProjectExtension> {
    jvmToolchain(21)
}

tasks.withType<KotlinCompile>().configureEach {
    compilerOptions.jvmTarget.set(JvmTarget.JVM_17)
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


// endregion

configure<KotlinProjectExtension> {
    explicitApi()
}

tasks.withType<KotlinCompile>().configureEach {
    compilerOptions.javaParameters.set(true)
    compilerOptions.freeCompilerArgs.addAll("-Xjsr305=strict")
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
