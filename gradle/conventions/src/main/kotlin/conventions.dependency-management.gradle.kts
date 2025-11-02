import Constants.DEPENDENCY_MANAGEMENT_CONFIG_NAME
import Constants.DEPENDENCY_MANAGEMENT_PROJECT_NAME
import io.syscall.gradle.conventions.isClasspathLike

/**
 * 프로젝트 전체에 의존성 버전 관리
 *
 * [Spring Boot: Dependency Versions](https://docs.spring.io/spring-boot/docs/3.1.0/reference/html/dependency-versions.html)
 * [Maven central](https://central.sonatype.com/artifact/org.springframework.boot/spring-boot-dependencies)
 * [소스](https://github.com/spring-projects/spring-boot/blob/v3.1.0/spring-boot-project/spring-boot-dependencies/build.gradle)
 * opentelemetry-java-instrumentation의 [otel.java-conventions](https://github.com/open-telemetry/opentelemetry-java-instrumentation/blob/3731b24a3252c94f66c62b414454342ec6c799e6/conventions/src/main/kotlin/otel.java-conventions.gradle.kts) 를 참고함.
 */
private object Comments

plugins {
    base
}

// region Dependency management 적용

private object Constants {
    const val DEPENDENCY_MANAGEMENT_CONFIG_NAME = "dependencyManagement"
    const val DEPENDENCY_MANAGEMENT_PROJECT_NAME = ":$DEPENDENCY_MANAGEMENT_CONFIG_NAME"
}

evaluationDependsOn(DEPENDENCY_MANAGEMENT_PROJECT_NAME)
val dependencyManagementConf =
    configurations.create(DEPENDENCY_MANAGEMENT_CONFIG_NAME) {
        isCanBeConsumed = false
        isCanBeResolved = false
    }

val includedConfigurations =
    setOf(
        // "${configuration.name}DependenciesMetadata",
        "DependenciesMetadata",

        // DevelopmentOnlyPlugin
        "devRuntimeOnly",

        // Spring Boot
        "developmentOnly",
        "productionRuntimeOnly",

        // Spring Boot AOT: "processAotClasspath", "processTestAotClasspath,
        "AotClasspath",

        "aggregateTestReportResults",
    )

fun shouldIncluded(c: Configuration): Boolean =
    (c.isCanBeResolved && !c.isCanBeConsumed && c.isClasspathLike) ||
        includedConfigurations.any { c.name.contains(it) }

afterEvaluate {
    // 필요함
    configurations
        .matching { shouldIncluded(it) }
        .configureEach {
            logger.info("Applying Dependency Management to {}", name)
            extendsFrom(dependencyManagementConf)
        }
}

dependencies {
    add(dependencyManagementConf.name, platform(project(DEPENDENCY_MANAGEMENT_PROJECT_NAME)))
}

// endregion
