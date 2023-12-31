import io.syscall.gradle.conventions.isClasspathLike

/**
 * Spring Boot 의존성 관리 적용
 *
 * [Spring Boot: Dependency Versions](https://docs.spring.io/spring-boot/docs/3.1.0/reference/html/dependency-versions.html)
 * [Maven central](https://central.sonatype.com/artifact/org.springframework.boot/spring-boot-dependencies)
 * [소스](https://github.com/spring-projects/spring-boot/blob/v3.1.0/spring-boot-project/spring-boot-dependencies/build.gradle)
 */
private object Comments

plugins {
    base
}

// region Dependency management 적용

evaluationDependsOn(":dependencyManagement:spring-boot")
val dependencyManagementConf: Configuration = configurations.dependencyScope("dependencyManagement.spring-boot").get()

val includedConfigurations = setOf(
    "devRuntimeOnly",
    "developmentOnly",
    "productionRuntimeOnly",

    // Spring Boot AOT
    "AotClasspath", // "processAotClasspath", "processTestAotClasspath,
)

fun shouldIncluded(c: Configuration): Boolean {
    return includedConfigurations.any { c.name.contains(it) }
}
afterEvaluate {
    configurations
        .matching { (it.isCanBeResolved && !it.isCanBeConsumed && it.isClasspathLike) || shouldIncluded(it) }
        .configureEach {
            logger.info("Applying dependencyManagement (Spring Boot) to ${name}")
            extendsFrom(dependencyManagementConf)
        }
}

dependencies {
    add(dependencyManagementConf.name, platform(project(":dependencyManagement:spring-boot")))
}

// endregion
