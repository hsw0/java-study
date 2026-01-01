plugins {
    id("conventions.project.java")
    id("conventions.project.java-analysis")
    id("conventions.project.spring-boot")
    `java-library`
    `java-test-fixtures`
}

dependencies {
    implementation("org.springframework.boot:spring-boot-persistence")
    implementation("org.springframework.boot:spring-boot-transaction")
    implementation("org.springframework.boot:spring-boot-jdbc")
    implementation("org.springframework.data:spring-data-jpa")
    implementation("org.hibernate.orm:hibernate-core")
    testRuntimeOnly("com.h2database:h2")

    implementation("org.springframework.data:spring-data-commons")
    implementation("org.springframework:spring-web")

    compileOnly("org.postgresql:postgresql")
}

configurations.compileClasspath {
    // Spring이 아닌 @jakarta.transaction.Transactional로 잘못 쓰는 것 방지
    exclude(group = "jakarta.transaction", module = "jakarta.transaction-api")
}
