import io.syscall.gradle.conventions.CustomJavaExtension
import io.syscall.gradle.util.ifPresent
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
}

tasks.withType<JavaCompile>().configureEach {
    customJavaExt.javaModuleName.ifPresent { javaModuleName ->
        inputs.property("javaModuleName", javaModuleName)
        val modulePath = sourceSets.main.get().output.asPath

        options.compilerArgs.addAll(listOf("--patch-module", "$javaModuleName=$modulePath"))
    }
}

//tasks.withType<Test>().configureEach {
//    customJavaExt.javaModuleName.ifPresent { javaModuleName ->
//        inputs.property("javaModuleName", javaModuleName)
//        val modulePath = sourceSets.main.get().output.asPath
//
//        jvmArgs(listOf("--patch-module", "$javaModuleName=$modulePath"))
//    }
//}

tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions.jvmTarget = DEFAULT_JAVA_VERSION.majorVersion
}

testing {
    @Suppress("UnstableApiUsage")
    suites.withType<JvmTestSuite>().configureEach {
        dependencies {
            implementation("org.jetbrains.kotlin:kotlin-test-junit5")
        }
    }
}

dependencies {
    implementation(kotlin("stdlib"))
}
