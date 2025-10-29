plugins {
    `java-library`
    `java-test-fixtures`

    id("conventions.base")
    id("conventions.dependency-management")
}

tasks.withType<JavaCompile>().configureEach {
    options.release = 17
}

dependencies {
    compileOnly(project(":module:annotations"))

    api("org.slf4j:slf4j-api")

    implementation("ch.qos.logback:logback-core")
    implementation("ch.qos.logback:logback-classic")

    runtimeOnly("org.slf4j:jcl-over-slf4j")
    runtimeOnly("org.slf4j:jul-to-slf4j")
}
