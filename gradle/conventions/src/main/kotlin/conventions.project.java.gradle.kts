import net.ltgt.gradle.errorprone.errorprone
import org.checkerframework.gradle.plugin.CheckerFrameworkExtension

/**
 * Java project convention
 *
 */
interface Comments

plugins {
    id("conventions.project.jvm")

    id("conventions.project.spotless")
    id("net.ltgt.errorprone")
    id("org.checkerframework")
}

dependencies {
    add("errorprone", "com.google.errorprone:error_prone_core")
    compileOnly("com.google.errorprone:error_prone_annotations")

    add("checkerFramework", platform(project(":dependencyManagement")))
    add("checkerFramework", "org.checkerframework:checker")
    compileOnly("org.checkerframework:checker-qual")
    testCompileOnly("org.checkerframework:checker-qual")
}

val versionCatalog = extensions.getByType<VersionCatalogsExtension>().named("libs")

val checkerFrameworkDep = versionCatalog.findLibrary("checkerframework").get().get().toString()
val checkerQualDep = versionCatalog.findLibrary("checkerframework-qual").get().get().toString()

configurations {
    "checkerFramework" {
        resolutionStrategy.dependencySubstitution {
            substitute(module("org.checkerframework:checker"))
                .using(module(checkerFrameworkDep))
        }
    }
    all {
        if (name.endsWith("Classpath", ignoreCase = true)
            || name.contains("AnnotationProcessor", ignoreCase = true)
        ) {
            resolutionStrategy.dependencySubstitution {
                substitute(module("org.checkerframework:checker-qual"))
                    .using(module(checkerQualDep))
            }
        }
    }
}

tasks.withType<JavaCompile>().configureEach {
    options.errorprone {
        disableWarningsInGeneratedCode.set(true)
        allDisabledChecksAsWarnings.set(true)

        warn("CollectionIncompatibleType")

        disable("MethodCanBeStatic")

        // https://github.com/open-telemetry/opentelemetry-java-instrumentation/blob/cd13fd40189d7297e953e68a8d2a4be1c68f56d9/conventions/src/main/kotlin/otel.errorprone-conventions.gradle.kts

        // checkerframework's NullnessChecker
        disable("FieldMissingNullable")
        disable("ParameterMissingNullable")
        disable("ReturnMissingNullable")
        disable("VoidMissingNullable")

        // MemberName

    }
}

configure<CheckerFrameworkExtension> {
    checkers = listOf(
        "org.checkerframework.checker.nullness.NullnessChecker",
        "org.checkerframework.checker.index.IndexChecker",
        "org.checkerframework.checker.tainting.TaintingChecker",
        "org.checkerframework.framework.util.PurityChecker",
    )

    excludeTests = true

    // error: [type.checking.not.run] ${NAME}Checker did not run because of a previous error issued by javac
    extraJavacArgs.add("-AsuppressWarnings=type.checking.not.run")
}
