import org.gradle.kotlin.dsl.support.serviceOf
import org.gradle.platform.Architecture
import org.gradle.platform.OperatingSystem

plugins {
    id("conventions.project.java")
    id("conventions.project.spring-boot")
    id("io.syscall.gradle.plugin.devonly")
    `java-library`
    `java-test-fixtures`
}

group = "dummy"

dependencies {
    compileOnly(project(":module:annotations"))

    testFixturesApi("io.projectreactor:reactor-test")
    testRuntimeOnly("io.projectreactor:reactor-tools")
    compileOnly("io.projectreactor.tools:blockhound") // BlockHoundIntegration SPI
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
