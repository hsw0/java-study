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

    // macOS 개발자 머신만 별도 처리
    if (System.getProperty("os.name") == "Mac OS X") {
        val classifier = when (val arch = System.getProperty("os.arch")) {
            "x86_64", "amd64" -> "osx-x86_64"
            "aarch64" -> "osx-aarch_64"
            else -> TODO("No macOS for $arch")
        }
        // ex) netty-transport-native-kqueue-${VERSION}-osx-aarch_64.jar
        runtimeOnly("io.netty:netty-transport-native-kqueue::$classifier")
        runtimeOnly("io.netty:netty-resolver-dns-native-macos::$classifier")
    }

    // Production
    for (arch in listOf("aarch_64", "x86_64")) {
        runtimeOnly("io.netty:netty-transport-native-epoll::linux-${arch}")
    }
}
