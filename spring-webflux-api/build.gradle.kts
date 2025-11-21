import org.springframework.boot.gradle.tasks.run.BootRun

plugins {
    id("conventions.project.kotlin")
    id("conventions.project.spring-boot-app")
    id("conventions.jpa-entity")
}


tasks.withType<BootRun>().configureEach {
    optimizedLaunch = false
    jvmArguments.add("-Dspring.aot.enabled=true")
}

dependencies {
    implementation(project(":module:entityid"))
    implementation(project(":module:entityid:hibernate"))

    implementation(project(":module:api-base"))
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    testImplementation("org.springframework.boot:spring-boot-webtestclient")

    implementation("com.google.guava:guava")

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core-jvm")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")

    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test")
    testRuntimeOnly("org.jetbrains.kotlinx:kotlinx-coroutines-debug")

    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("com.h2database:h2")
    implementation(project(":module:persistence-support"))
}

configurations.compileClasspath {
    // Spring이 아닌 @jakarta.transaction.Transactional로 잘못 쓰는 것 방지
    exclude(group = "jakarta.transaction", module = "jakarta.transaction-api")
}
