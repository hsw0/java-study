rootProject.name = "java-study"

includeBuild("gradle/conventions")

include(":dependencyManagement")
project(":dependencyManagement").buildFileName = "../gradle/dependency.gradle.kts"

include(":study-place")
