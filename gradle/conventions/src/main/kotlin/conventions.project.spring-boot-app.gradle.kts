/**
 * Spring Boot Application convention
 *
 */
private object Comments

plugins {
    id("conventions.project.spring-boot")
    id("org.springframework.boot")
    id("org.springframework.boot.aot")
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.springframework.boot:spring-boot-starter-aop")
    implementation("org.springframework.boot:spring-boot-starter-validation")

    developmentOnly("io.projectreactor:reactor-tools")
    developmentOnly("io.projectreactor.tools:blockhound")

    implementation(project(":module:springboot-app-base"))
    testImplementation(testFixtures(project(":module:springboot-app-base")))
}

tasks.withType<JavaCompile>().named { it.contains("Aot") }.configureEach {
    options.compilerArgs.removeIf { it.startsWith("-Xlint:") }
    options.compilerArgs.add("-Xlint:none")
}
