plugins {
    id("conventions.project.java")
    id("conventions.project.spring-boot")
    `java-library`
    `java-test-fixtures`
}

group = "dummy"

dependencies {
    compileOnly(project(":module:annotations"))

    implementation("org.springframework.data:spring-data-jpa")
    implementation("org.hibernate.orm:hibernate-core")
    testRuntimeOnly("com.h2database:h2")

    implementation("org.springframework.data:spring-data-commons")
    implementation("org.springframework:spring-web")
}

configurations.compileClasspath {
    // Spring이 아닌 @jakarta.transaction.Transactional로 잘못 쓰는 것 방지
    exclude(group = "jakarta.transaction", module = "jakarta.transaction-api")
}
