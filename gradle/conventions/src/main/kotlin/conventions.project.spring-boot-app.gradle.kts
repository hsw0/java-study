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
    implementation(project(":module:springboot-app-base"))
    testImplementation(testFixtures(project(":module:springboot-app-base")))

    implementation("org.springframework.boot:spring-boot")
    implementation("org.springframework.boot:spring-boot-autoconfigure")

    developmentOnly("io.projectreactor:reactor-tools")
    developmentOnly("io.projectreactor.tools:blockhound")
}

tasks.withType<JavaCompile>().named { it.contains("Aot") }.configureEach {
    options.compilerArgs.removeIf { it.startsWith("-Xlint:") }
    options.compilerArgs.add("-Xlint:none")
}
