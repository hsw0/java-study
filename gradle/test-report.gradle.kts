@file:Suppress("UnstableApiUsage")

/**
 * 통합 Report
 *
 * @see <a href="https://docs.gradle.org/current/userguide/test_report_aggregation_plugin.html">Test Report Aggregation Plugin</a>
 * @see <a href="https://docs.gradle.org/current/userguide/jacoco_report_aggregation_plugin.html">JaCoCo Report Aggregation Plugin</a>
 */
interface Comments

plugins {
    id("conventions.java-base")
    id("conventions.dependency-management")

    `test-report-aggregation`
    `jacoco-report-aggregation`
}
/*
얘들은 이상한 오류가 발생:
**`java-test-fixtures` 플러그인이 있고 kotlin 플러그인이 없으면 발생한다**

Could not determine the dependencies of task ':test-report:jacocoTestReport'.
> Could not resolve all task dependencies for configuration ':test-report:aggregateCodeCoverageReportResults'.
   > The consumer was configured to find a component, preferably in the form of class files. However we cannot choose between the following variants of project ${project.path}:
       - Configuration '${project.path}:apiElements' variant classes declares a component, preferably in the form of class files:
           - Unmatched attributes:
               - Provides attribute 'artifactType' with value 'java-classes-directory' but the consumer didn't ask for it

     The following variants were also considered but didn't match the requested attributes:
       - Configuration '${project.path}:runtimeElements' variant resources:
           - Incompatible because this component declares a component, preferably only the resources files and the consumer needed a component, preferably in the form of class files
*/
val jaCoCoExcludedProjects = setOf(
    ":module:logging-support",
    ":module:jpa-support",
    ":module:reactor-support",
    ":module:springboot-app-base",
)

val projectsList = rootProject.allprojects.filterNot { it.path == project.path }

for (targetProject in projectsList) {
    evaluationDependsOn(targetProject.path)
}


afterEvaluate {
    dependencies {
        for (targetProject in projectsList) {
            if (!targetProject.pluginManager.hasPlugin("jvm-test-suite")) {
                continue
            }

            testReportAggregation(targetProject)
            if (targetProject.pluginManager.hasPlugin("jacoco")) {
                with(jacocoAggregation(targetProject) as ProjectDependency) {
                    this.isTransitive = false
                }
            }
        }
    }
}

reporting {
    reports {
        create<AggregateTestReport>("test") {
            testType.set(TestSuiteType.UNIT_TEST)
        }

        create<JacocoCoverageReport>("jacocoTestReport") {
            testType.set(TestSuiteType.UNIT_TEST)

            reportTask {
                reports {
                    html.required.set(true)
                    xml.required.set(true)
                }
            }
        }
    }
}
