/**
 * Spotless
 *
 */
interface Comments

plugins {
    id("com.diffplug.spotless")
}

afterEvaluate {
    if (pluginManager.hasPlugin("java")) {
        spotless.java {
            palantirJavaFormat("2.33.0")
        }
    }

    if (pluginManager.hasPlugin("org.jetbrains.kotlin.jvm")) {
        spotless.kotlin {
            with(ktlint()) {
                editorConfigOverride(
                    mapOf(

                    ),
                )
            }
        }
    }
}
