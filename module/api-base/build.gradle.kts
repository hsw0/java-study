plugins {
    id("conventions.project.java")
    id("conventions.project.spring-boot")
    `java-library`
}

group = "dummy"

dependencies {
    compileOnly(project(":module:annotations"))

    implementation(project(":module:protocol"))
    implementation(project(":module:springboot-app-base"))

    implementation("org.springframework:spring-web")
    implementation("org.springframework:spring-webflux")
}
