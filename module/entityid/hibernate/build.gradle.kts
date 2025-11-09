plugins {
    id("conventions.project.java")
    id("conventions.project.java-analysis")
    id("conventions.project.kotlin")
    id("conventions.publishing")
    `java-library`
}

group = "io.syscall.commons"
version = "1.0-SNAPSHOT"

extra["publishing.artifactId"] = "entityid-hibernate"

dependencies {
    api(project(":module:entityid"))

    implementation("org.hibernate.orm:hibernate-core")
}
