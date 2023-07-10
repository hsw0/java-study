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
