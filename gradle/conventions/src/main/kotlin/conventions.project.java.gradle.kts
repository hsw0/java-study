/**
 * Java project convention
 *
 */
interface Comments

plugins {
    id("conventions.project.base")
    id("conventions.java")
}

dependencies {
    implementation("org.slf4j:slf4j-api")

    testRuntimeOnly("ch.qos.logback:logback-core")
    testRuntimeOnly("ch.qos.logback:logback-classic")
}
