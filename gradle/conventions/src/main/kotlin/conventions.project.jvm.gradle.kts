/**
 * JVM based project convention
 *
 */
private object Comments

plugins {
    id("conventions.project.base")
    id("conventions.java")
}

dependencies {
    implementation("org.slf4j:slf4j-api:[2.0,)") // Who doesn't?

    // Test framework의 의존성은 아니지만 공통으로 사용
    testImplementation("org.assertj:assertj-core")
}

configurations.configureEach {
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
