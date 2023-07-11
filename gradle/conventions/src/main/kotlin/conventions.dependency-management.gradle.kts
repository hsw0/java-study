/**
 * 프로젝트 전체에 의존성 버전 관리
 *
 */
private object Comments

plugins {
    base
}

// region Dependency management 적용
// opentelemetry-java-instrumentation의 [otel.java-conventions](https://github.com/open-telemetry/opentelemetry-java-instrumentation/blob/cd13fd40189d7297e953e68a8d2a4be1c68f56d9/conventions/src/main/kotlin/otel.java-conventions.gradle.kts) 를 참고함.

evaluationDependsOn(":dependencyManagement")
val dependencyManagementConf: Configuration = configurations.create("dependencyManagement") {
    isCanBeConsumed = false
    isCanBeResolved = false
    isVisible = false
}

afterEvaluate {
    configurations.configureEach {
        if (isCanBeResolved && !isCanBeConsumed) {
            logger.info("Applying dependencyManagement to ${name}")
            extendsFrom(dependencyManagementConf)
        }
    }
}

dependencies {
    add(dependencyManagementConf.name, platform(project(":dependencyManagement")))
}

// endregion
