
import com.diffplug.gradle.spotless.SpotlessExtension
import com.diffplug.gradle.spotless.SpotlessExtensionPredeclare

/**
 * Spotless
 *
 * Based on [otel.spotless-conventions](https://github.com/open-telemetry/opentelemetry-java-instrumentation/blob/bba8a073901f6ffd3e415e2570efda075656468d/conventions/src/main/kotlin/otel.spotless-conventions.gradle.kts)
 */
private object Comments

plugins {
    id("com.diffplug.spotless")
}

if (project != rootProject) {
    throw UnsupportedOperationException("Root project only!")
}

@Suppress("detekt:MaxLineLength")
/*
Workaround

> Could not resolve all files for configuration ':spotless-<RAND>'.
   > Could not resolve com.google.guava:guava:32.1.2-jre.
     Required by:
         project : > com.palantir.javaformat:palantir-java-format:2.35.0
         project : > com.palantir.javaformat:palantir-java-format:2.35.0 > com.palantir.javaformat:palantir-java-format-spi:2.35.0
      > The consumer was configured to find attribute 'org.gradle.dependency.bundling' with value 'external'. However we cannot choose between the following variants of com.google.guava:guava:32.1.2-jre:
   > Could not resolve com.google.guava:guava:23.6.1-jre.
     Required by:
         project : > com.palantir.javaformat:palantir-java-format:2.35.0 > com.palantir.javaformat:palantir-java-format-spi:2.35.0 > com.fasterxml.jackson.datatype:jackson-datatype-guava:2.15.2
      > The consumer was configured to find attribute 'org.gradle.dependency.bundling' with value 'external'. However we cannot choose between the following variants of com.google.guava:guava:32.1.2-jre:
          - androidRuntimeElements
          - jreRuntimeElements
*/

(configurations.matching { it.name.startsWith("spotless-") }).whenObjectAdded {
    val configuration = this
    configuration.dependencies.matching { it.group == "com.palantir.javaformat" }.whenObjectAdded {
        require(this is ExternalModuleDependency)
        exclude("com.google.guava", "guava")
    }

    val guavaDep = project.dependencies.create("com.google.guava:guava:32.1.2-jre") as ExternalModuleDependency
    guavaDep.isTransitive = false
    guavaDep.attributes {
        attribute(
            TargetJvmEnvironment.TARGET_JVM_ENVIRONMENT_ATTRIBUTE,
            project.objects.named<TargetJvmEnvironment>(TargetJvmEnvironment.STANDARD_JVM),
        )
    }
    configuration.dependencies.add(guavaDep)
}

configure<SpotlessExtension> {
    predeclareDeps()
}

configure<SpotlessExtensionPredeclare> {
    java {
        palantirJavaFormat("2.35.0")
    }
    kotlin {
        ktlint()
    }
    kotlinGradle {
        ktlint()
    }
}
