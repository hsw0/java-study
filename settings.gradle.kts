rootProject.name = "java-study"

enableFeaturePreview("STABLE_CONFIGURATION_CACHE")

pluginManagement {
    // [Favor build-logic Composite Builds for Build Logic](https://docs.gradle.org/9.3.1/userguide/best_practices_structuring_builds.html#favor_composite_builds)
    // buildSrc 사용 안함
    includeBuild("gradle/conventions") {
        name = "conventions"
    }
    // Default:
    // repositories { gradlePluginPortal() }
}

dependencyResolutionManagement {
    @Suppress("UnstableApiUsage")
    repositoriesMode = RepositoriesMode.FAIL_ON_PROJECT_REPOS

    @Suppress("UnstableApiUsage")
    repositories {
        mavenCentral()
    }
}

include(":dependencyManagement")
project(":dependencyManagement").projectDir = file("./gradle/dependencyManagement")

include(":test-report")

// /////////////////////////////////////////////////////////////////////////////

// Internal utilities
include(
    ":module:annotations",
    ":module:logging-support",
)

// Reusable module
include(
    ":module:entityid",
    ":module:entityid:hibernate",
)

// Project-specific component w/o business logic
include(
    ":module:protocol",
    ":module:reactor-support",
    ":module:persistence-support",
    ":module:springboot-support",
    ":module:api-base",
    ":module:springboot-app-base",
    ":module:jackson-support",
)

// /////////////////////////////////////////////////////////////////////////////

include(":analysis-example") // errorprone, checker 등

include(
    ":ksink",
    ":dummy:lib",
    ":dummy:app",
)

include(
    ":domain:sample-domain:model",
    ":domain:sample-domain:datasource",
    ":domain:sample-domain:persistence",
    ":domain:sample-domain:business",
    ":spring-webflux-api",
)
