plugins {
    id("conventions.project.kotlin")
    `java-library`
    `java-test-fixtures`
}

group = "dummy"

dependencies {
    compileOnly(project(":module:annotations"))

    implementation(project(":module:entityid"))
}
