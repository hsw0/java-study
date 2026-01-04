rootProject.name = "java-study"

enableFeaturePreview("STABLE_CONFIGURATION_CACHE")

pluginManagement {
    includeBuild("gradle/conventions") {
        name = "conventions"
    }
    // Default:
    //repositories { gradlePluginPortal() }
}

dependencyResolutionManagement {
    @Suppress("UnstableApiUsage")
    repositories {
        mavenCentral()
    }
}

include(":dependencyManagement")
project(":dependencyManagement").projectDir = file("./gradle/dependencyManagement")


include(":test-report")

///////////////////////////////////////////////////////////////////////////////

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
)

///////////////////////////////////////////////////////////////////////////////

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
