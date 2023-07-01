/**
 * Gradle java-base plugin config
 *
 * @see org.gradle.api.plugins.JavaBasePlugin
 */
interface ConventionJavaBase

plugins {
    `java-base`
}

project.ext["DEFAULT_JAVA_VERSION"] = JavaVersion.VERSION_17
