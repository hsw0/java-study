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
}

