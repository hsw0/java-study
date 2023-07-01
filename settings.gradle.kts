rootProject.name = "java-study"

includeBuild("gradle/conventions")

include(":dependencyManagement")
project(":dependencyManagement").buildFileName = "../gradle/dependency.gradle.kts"

include(":test-report")
project(":test-report").buildFileName = "../gradle/test-report.gradle.kts"

///////////////////////////////////////////////////////////////////////////////

include(":dummy-library")
include(":dummy-app")
