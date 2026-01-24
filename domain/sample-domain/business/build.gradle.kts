
plugins {
    id("conventions.project.kotlin")
    id("conventions.project.spring-boot")
    `java-library`
}


dependencies {
    implementation(project(":domain:sample-domain:persistence"))
    api(project(":domain:sample-domain:model"))

    implementation("org.springframework:spring-tx")
}
