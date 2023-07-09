rootProject.name = "java-study"

pluginManagement {
    includeBuild("gradle/conventions") {
        name = "conventions"
    }
}

dependencyResolutionManagement {
    repositories {
        mavenCentral()
    }
}

include(":dependencyManagement")
project(":dependencyManagement").buildFileName = "../gradle/dependency.gradle.kts"

include(":test-report")
project(":test-report").buildFileName = "../gradle/test-report.gradle.kts"

///////////////////////////////////////////////////////////////////////////////

// Internal utilities
include(
    ":module:annotations",
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
    ":module:jpa-support",
    ":module:springboot-support",
    ":module:api-base",
    ":module:springboot-app-base",
)

///////////////////////////////////////////////////////////////////////////////

include(":analysis-example") // errorprone, checker 등

include(
    ":dummy:lib",
    ":dummy:app",
)

include(":spring-webflux-api")
