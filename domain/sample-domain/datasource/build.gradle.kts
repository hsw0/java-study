
plugins {
    id("conventions.project.kotlin")
    id("conventions.project.spring-boot")
    `java-library`
}


dependencies {
    implementation(project(":module:persistence-support"))
    implementation("org.springframework.boot:spring-boot-jdbc")
    implementation("com.zaxxer:HikariCP")
    implementation("com.h2database:h2")
}
