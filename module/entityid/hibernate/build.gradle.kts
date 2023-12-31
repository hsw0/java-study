plugins {
    id("conventions.project.java")
    id("conventions.project.kotlin")
    id("conventions.publishing")
    `java-library`
}

group = "io.syscall.commons"
version = "1.0-SNAPSHOT"

ext["publishing.artifactId"] = "entityid-hibernate"

tasks.withType<JavaCompile>().configureEach {
    options.release.set(17)
}

dependencies {
    compileOnly(project(":module:annotations"))
    testRuntimeOnly(testFixtures(project(":module:logging-support")))

    api(project(":module:entityid"))

    implementation("org.hibernate.orm:hibernate-core:[6.2,)")

    testImplementation("io.github.oshai:kotlin-logging-jvm")
}
