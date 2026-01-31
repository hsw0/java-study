plugins {
    id("conventions.project.java")
    `java-library`
    `java-test-fixtures`
}

dependencies {
    compileOnly("com.google.errorprone:error_prone_annotations")

    api("com.fasterxml.jackson.core:jackson-annotations")
    implementation("tools.jackson.core:jackson-core")
    api("tools.jackson.core:jackson-databind")
    implementation("tools.jackson.module:jackson-module-kotlin")
}
