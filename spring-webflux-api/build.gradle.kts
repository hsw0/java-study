import org.gradle.kotlin.dsl.support.serviceOf
import org.gradle.platform.Architecture
import org.gradle.platform.OperatingSystem

plugins {
    id("conventions.project.kotlin")
    id("conventions.project.spring-boot-app")
    id("io.syscall.gradle.plugin.devonly")
}

group = "io.syscall.hsw.study"
version = "1.0-SNAPSHOT"

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-webflux")

    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("com.google.guava:guava")

    implementation("io.github.oshai:kotlin-logging-jvm")

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core-jvm")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")

    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test")
    devRuntimeOnly("org.jetbrains.kotlinx:kotlinx-coroutines-debug")
    testRuntimeOnly("org.jetbrains.kotlinx:kotlinx-coroutines-debug")

    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("io.micrometer:micrometer-registry-prometheus")

    testImplementation("io.projectreactor:reactor-test")
    devRuntimeOnly("io.projectreactor:reactor-tools")
    testRuntimeOnly("io.projectreactor:reactor-tools")
    compileOnly("io.projectreactor.tools:blockhound") // BlockHoundIntegration SPI
    devRuntimeOnly("io.projectreactor.tools:blockhound")
    testRuntimeOnly("io.projectreactor.tools:blockhound")

    @Suppress("UnstableApiUsage")
    if (serviceOf<BuildPlatform>().operatingSystem == OperatingSystem.MAC_OS) {
        val classifier = when (val arch = serviceOf<BuildPlatform>().architecture) {
            Architecture.X86_64 -> "osx-x86_64"
            Architecture.AARCH64 -> "osx-aarch_64"
            else -> TODO("No macOS for $arch")
        }
        // ex) netty-transport-native-kqueue-${VERSION}-osx-aarch_64.jar
        runtimeOnly(group = "io.netty", name = "netty-transport-native-kqueue", classifier = classifier)
        runtimeOnly(group = "io.netty", name = "netty-resolver-dns-native-macos", classifier = classifier)
    }

    // Production
    for (arch in listOf("aarch_64", "x86_64")) {
        runtimeOnly(group = "io.netty", name = "netty-transport-native-epoll", classifier = "linux-${arch}")
    }
}


