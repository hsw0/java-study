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

configurations {
    all {
        if (name.endsWith("CompileClasspath", ignoreCase = true)) {
            exclude("org.apache.logging.log4j", "log4j-api")
            exclude("org.apache.logging.log4j", "log4j-core")
        }
        if (name.endsWith("RuntimeClasspath", ignoreCase = true)) {
            resolutionStrategy.dependencySubstitution {
                substitute(module("org.apache.logging.log4j:log4j-api"))
                    .using(module("org.slf4j:log4j-over-slf4j:2.+"))
                substitute(module("org.apache.logging.log4j:log4j-to-slf4j"))
                    .using(module("org.slf4j:log4j-over-slf4j:2.+"))
            }
        }
    }
}
