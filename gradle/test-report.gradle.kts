@file:Suppress("UnstableApiUsage")

import io.syscall.gradle.plugin.jacoco.JacocoMergeTask

/**
 * 통합 Report
 *
 * @see <a href="https://docs.gradle.org/current/userguide/test_report_aggregation_plugin.html">Test Report Aggregation Plugin</a>
 * @see <a href="https://docs.gradle.org/current/userguide/jacoco_report_aggregation_plugin.html">JaCoCo Report Aggregation Plugin</a>
 */
private object Comments

plugins {
    id("conventions.java-base")
    id("conventions.dependency-management")

    `test-report-aggregation`
    `jacoco-report-aggregation`
}

jacoco {
    // 주의: 이거 덤프뜬 곳(각 프로젝트)과 병합한 곳(여기)의 버전이 불일치할 경우 IntelliJ 에서 병합된 .exec를 로드시 누락이 발생하는 것으로 보임
    toolVersion = libs.versions.jacoco.get()
}

val projectsList = rootProject.allprojects.filterNot { it.path == project.path }
val jaCoCoProjectsList = mutableSetOf<Project>()

for (targetProject in projectsList) {
    evaluationDependsOn(targetProject.path)
}

dependencies {
    for (targetProject in projectsList) {
        if (!targetProject.pluginManager.hasPlugin("jvm-test-suite")) {
            continue
        }

        testReportAggregation(targetProject)

        if (targetProject.pluginManager.hasPlugin("jacoco")) {
            jaCoCoProjectsList.add(targetProject)
        }
    }

    for (targetProject in jaCoCoProjectsList) {
        with(jacocoAggregation(targetProject) as ProjectDependency) {
            isTransitive = false
        }
    }
}

val defaultCheckTask = tasks.getByName(LifecycleBasePlugin.CHECK_TASK_NAME)

reporting.reports.create<AggregateTestReport>("testReport") {
    testType.set(TestSuiteType.UNIT_TEST)
    reportTask {
        group = LifecycleBasePlugin.VERIFICATION_GROUP
        defaultCheckTask.dependsOn(this)
    }
}

val jacocoTestReport by reporting.reports.creating(JacocoCoverageReport::class) {
    testType.set(TestSuiteType.UNIT_TEST)

    reportTask {
        group = LifecycleBasePlugin.VERIFICATION_GROUP
        defaultCheckTask.dependsOn(this)

        reports {
            html.required.set(true)
            xml.required.set(true)
        }
    }
}

val jacocoMergeExecution by tasks.creating(JacocoMergeTask::class) {
    group = LifecycleBasePlugin.VERIFICATION_GROUP
    defaultCheckTask.dependsOn(this)

    dependsOn(jacocoTestReport.reportTask)
    executionData.from(jacocoTestReport.reportTask.map { it.executionData })
    jacocoClasspath = configurations[JacocoPlugin.ANT_CONFIGURATION_NAME]
}
