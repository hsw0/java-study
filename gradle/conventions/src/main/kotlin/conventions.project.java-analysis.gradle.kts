
import net.ltgt.gradle.errorprone.errorprone
import org.checkerframework.gradle.plugin.CheckerFrameworkExtension
import org.checkerframework.gradle.plugin.CreateManifestTask

/**
 * Java static analyzers
 *
 */
private object Comments

plugins {
    id("conventions.dependency-management")

    id("net.ltgt.errorprone") apply false
    id("org.checkerframework") apply false
}

pluginManager.withPlugin("java") {
    apply(plugin = "net.ltgt.errorprone")
    apply(plugin = "org.checkerframework")

    dependencies {
        val compileOnly by configurations
        val testCompileOnly by configurations

        add("errorprone", "com.google.errorprone:error_prone_core")
        compileOnly("com.google.errorprone:error_prone_annotations")

        add("checkerFramework", platform(project(":dependencyManagement")))
        add("checkerFramework", "org.checkerframework:checker")
        compileOnly("org.checkerframework:checker-qual")
        testCompileOnly("org.checkerframework:checker-qual")
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

            disable("AndroidJdkLibsChecker")
            disable("Java7ApiChecker")
            disable("Java8ApiChecker")

            // If you don't intend to mutate a member collection prefer using Immutable types.
            // guava ImmutableMap 등을 추천함
            disable("ImmutableMemberCollection")

            // Non-constant variable missing @Var annotation
            disable("Var")

            // Type Parameter DB must be a single letter with an optional numeric suffix, or an UpperCamelCase name followed by the letter 'T'.
            disable("TypeParameterNaming")

            // Since Java 8, it's been unnecessary to make local variables and parameters `final` for use in lambdas or anonymous classes. Marking them as `final` is weakly discouraged, as it adds a fair amount of noise for minimal benefit.
            // ex) "final var variableName", "final String str"
            disable("UnnecessaryFinal")
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

    tasks.withType<CreateManifestTask>().configureEach {
        // Task `:${PROJECT}:createCheckerFrameworkManifest` of type `org.checkerframework.gradle.plugin.CreateManifestTask`: invocation of 'Task.project' at execution time is unsupported.
        notCompatibleWithConfigurationCache("UNSUPPORTED")
    }
}
