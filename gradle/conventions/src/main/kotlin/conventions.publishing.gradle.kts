/**
 * Maven publishing
 *
 * @see org.gradle.api.publish.maven.plugins.MavenPublishPlugin
 */
interface Comments

plugins {
    java
    `maven-publish`
}

java {
    withJavadocJar()
    withSourcesJar()
}

publishing {
    publications {
        create<MavenPublication>("artifact") {
            plugins.withId("java-library") {
                from(components["java"])
            }

            versionMapping {
                allVariants {
                    fromResolutionResult()
                }
            }
        }
    }
}
