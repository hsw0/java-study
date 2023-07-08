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

include(
    ":module:annotations",
    ":module:entityid",
    ":module:entityid:hibernate",
    ":module:reactor-support",
    ":module:springboot-app-base",
)

///////////////////////////////////////////////////////////////////////////////

include(":analysis-example") // errorprone, checker 등

include(
    ":dummy:lib",
    ":dummy:app",
)

include(":spring-webflux-api")
