plugins {
    id("conventions.project.java")
    `java-library`
    `java-test-fixtures`
}

group = "dummy"

dependencies {
    compileOnly(project(":module:annotations"))

    api("org.slf4j:slf4j-api:[2.0,)") // Who doesn't?

    implementation("ch.qos.logback:logback-core")
    implementation("ch.qos.logback:logback-classic")

    runtimeOnly("org.slf4j:log4j-over-slf4j")
    runtimeOnly("org.slf4j:jcl-over-slf4j")
    runtimeOnly("org.slf4j:jul-to-slf4j")
}
