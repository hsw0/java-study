plugins {
    id("conventions.project.java")
    id("conventions.project.java-analysis")
    id("conventions.project.kotlin")
    id("conventions.project.spring-boot")
    `java-library`
    `java-test-fixtures`
}

dependencies {
    implementation(project(":module:springboot-support"))

    implementation("org.springframework.boot:spring-boot-persistence")
    implementation("org.springframework.boot:spring-boot-transaction")
    implementation("org.springframework.boot:spring-boot-jdbc")
    implementation("org.springframework.boot:spring-boot-hibernate")
    implementation("org.springframework.boot:spring-boot-jpa")
    implementation("org.springframework.data:spring-data-jpa")
    implementation("org.hibernate.orm:hibernate-core")

    implementation("org.springframework.data:spring-data-commons")
    implementation("org.springframework:spring-web")

    compileOnly("org.postgresql:postgresql")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.boot:spring-boot-starter-data-jpa")
    testRuntimeOnly("com.h2database:h2")

    compileOnly("com.google.errorprone:error_prone_annotation:2.45.0")
}

configurations.compileClasspath {
    // Spring이 아닌 @jakarta.transaction.Transactional로 잘못 쓰는 것 방지
    exclude(group = "jakarta.transaction", module = "jakarta.transaction-api")
}
