
plugins {
    id("conventions.project.kotlin")
    id("conventions.jpa-entity")
    `java-library`
}


dependencies {
    api(project(":module:entityid"))
    implementation(project(":module:persistence-support"))

    // JPA annotations for entity
    compileOnly("org.hibernate.orm:hibernate-core")
    compileOnly("jakarta.persistence:jakarta.persistence-api")
}
