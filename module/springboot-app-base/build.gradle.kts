plugins {
    id("conventions.project.java")
    id("conventions.project.kotlin")
    id("conventions.project.spring-boot")
    `java-library`
    `java-test-fixtures`
}

group = "dummy"

dependencies {
    compileOnly(project(":module:annotations"))

    // conventions.project.spring-boot-app 과 중복
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.springframework.boot:spring-boot-starter-aop")
    implementation("org.springframework.boot:spring-boot-starter-validation")

    implementation("org.springframework.cloud:spring-cloud-context") {
        exclude(group = "org.springframework.security", module = "spring-security-crypto")
    }

    // 모든 프로젝트는 일단 모니터링용으로 HTTP를 사용한다
    implementation("org.springframework.boot:spring-boot-starter-webflux")

    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("io.micrometer:micrometer-registry-prometheus")
    runtimeOnly("io.micrometer:micrometer-core")
    runtimeOnly("io.micrometer:micrometer-tracing")

    implementation(project(":module:reactor-support"))

    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
}
