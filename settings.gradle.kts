rootProject.name = "java-study"

includeBuild("gradle/conventions")

include(":dependencyManagement")
project(":dependencyManagement").buildFileName = "../gradle/dependency.gradle.kts"

include(":test-report")
project(":test-report").buildFileName = "../gradle/test-report.gradle.kts"

///////////////////////////////////////////////////////////////////////////////

include(":analysis-example") // errorprone, checker 등

include(
    ":dummy:lib",
    ":dummy:app",
)

include(":module:java21-compat")
include(":spring-webflux-api")
