/**
 * Java project convention
 *
 */
private object Comments

plugins {
    id("conventions.project.jvm")

    id("conventions.project.spotless")
    id("conventions.project.java-analysis")
}

tasks.withType<Javadoc> {
    when (val it = options) {
        is CoreJavadocOptions -> it.addStringOption("Xdoclint:none", "-quiet")
    }
}
