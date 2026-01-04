
plugins {
    id("conventions.project.kotlin")
    id("conventions.project.spring-boot")
    `java-library`
}


dependencies {
    implementation(project(":module:persistence-support"))
    implementation(project(":module:entityid:hibernate"))

    implementation(project(":domain:sample-domain:datasource"))
    api(project(":domain:sample-domain:model"))

    api("org.springframework.data:spring-data-jpa")
    implementation("org.springframework.boot:spring-boot-hibernate")
}
