import io.syscall.gradle.conventions.CustomJavaExtension
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
