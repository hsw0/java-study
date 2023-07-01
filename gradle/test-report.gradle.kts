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

dependencies {
    for (subproject in rootProject.subprojects) {
        if (!subproject.pluginManager.hasPlugin("jvm-test-suite")) {
            continue
        }
        testReportAggregation(subproject)
        if (subproject.pluginManager.hasPlugin("jacoco")) {
            jacocoAggregation(subproject)
        }
    }
}

reporting {
    reports {
        create<AggregateTestReport>("test") {
            testType.set(TestSuiteType.UNIT_TEST)
        }

        create<AggregateTestReport>("integrationTest") {
            testType.set(TestSuiteType.INTEGRATION_TEST)
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

        create<JacocoCoverageReport>("jacocoIntegrationTestReport") {
            testType.set(TestSuiteType.INTEGRATION_TEST)

            reportTask {
                reports {
                    html.required.set(true)
                    xml.required.set(true)
                }
            }
        }

    }
}
