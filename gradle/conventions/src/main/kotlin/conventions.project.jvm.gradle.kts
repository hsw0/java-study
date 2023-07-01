/**
 * JVM based project convention
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
    testRuntimeOnly("org.slf4j:log4j-over-slf4j")
    testRuntimeOnly("org.slf4j:jcl-over-slf4j")
    testRuntimeOnly("org.slf4j:jul-to-slf4j")

    testImplementation("org.assertj:assertj-core")
}
