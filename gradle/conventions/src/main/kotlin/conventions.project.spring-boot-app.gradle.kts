import org.springframework.boot.gradle.tasks.aot.ProcessAot
import org.springframework.boot.gradle.tasks.bundling.BootJar

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
}

tasks.withType<BootJar>().configureEach {
    // > Execution of task ':${PROJECT}:bootJar' caused invocation of 'Task.project' by task ':${PROJECT}:resolveMainClassName' at execution time which is unsupported.
    notCompatibleWithConfigurationCache("UNSUPPORTED")
}

tasks.withType<ProcessAot>().configureEach {
    notCompatibleWithConfigurationCache("Invokes other task: resolveMainClassName")
}
