/**
 * Java project convention
 *
 */
private object Comments

plugins {
    id("conventions.project.jvm")

    id("conventions.project.spotless")
}

tasks.withType<Javadoc> {
    when (val it = options) {
        is CoreJavadocOptions -> it.addStringOption("Xdoclint:none", "-quiet")
    }
}

dependencies {
    compileOnly("org.jspecify:jspecify:1.0.0")
    testCompileOnly("org.jspecify:jspecify:1.0.0")

    implementation("org.slf4j:slf4j-api") // Who doesn't?
}
