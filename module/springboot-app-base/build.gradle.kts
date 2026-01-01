plugins {
    id("conventions.project.java")
    id("conventions.project.java-analysis")
    id("conventions.project.spring-boot")
    `java-library`
    `java-test-fixtures`
}

group = "dummy"

dependencies {
    implementation(project(":module:logging-support"))
    implementation(project(":module:springboot-support"))
    implementation(project(":module:reactor-support"))

    // conventions.project.spring-boot-app 과 약간 겹치는 선언
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.springframework.boot:spring-boot-starter-aspectj")
    implementation("org.springframework.boot:spring-boot-validation")
    runtimeOnly("io.micrometer:context-propagation")
    runtimeOnly("io.opentelemetry:opentelemetry-context")

    runtimeOnly("org.springframework.boot:spring-boot-jackson")
    runtimeOnly("tools.jackson.module:jackson-module-kotlin")

    implementation("org.springframework.cloud:spring-cloud-context") {
        exclude(group = "org.springframework.security", module = "spring-security-crypto")
    }

    // 모든 프로젝트는 일단 모니터링용으로 HTTP를 사용한다
    implementation("org.springframework.boot:spring-boot-web-server")
    implementation("org.springframework.boot:spring-boot-starter-webflux")

    compileOnly("org.springframework.boot:spring-boot-webmvc")
    compileOnly("jakarta.servlet:jakarta.servlet-api")
    testImplementation("org.springframework.boot:spring-boot-starter-web")
    testImplementation("org.springframework.boot:spring-boot-webtestclient")

    // spring-boot-starter-actuator:
    implementation("org.springframework.boot:spring-boot-actuator-autoconfigure")
    implementation("org.springframework.boot:spring-boot-micrometer-metrics")
    runtimeOnly("org.springframework.boot:spring-boot-health")

    testFixturesApi("org.springframework.boot:spring-boot-starter-test")
    testFixturesApi("org.springframework.boot:spring-boot-starter-webflux")
}
