plugins {
    id("conventions.project.java")
    `java-library`
    `java-test-fixtures`
}

version = "1.0-SNAPSHOT"

tasks.withType<JavaCompile>().configureEach {
    options.release.set(11)
}

dependencies {
    compileOnly(project(":module:annotations"))

    api("org.slf4j:slf4j-api:[2.0,)")

    implementation("ch.qos.logback:logback-core")
    implementation("ch.qos.logback:logback-classic")

    runtimeOnly("org.slf4j:log4j-over-slf4j")
    runtimeOnly("org.slf4j:jcl-over-slf4j")
    runtimeOnly("org.slf4j:jul-to-slf4j")
}
