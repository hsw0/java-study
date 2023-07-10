@file:Suppress("UnstableApiUsage")

import io.syscall.gradle.plugin.jacoco.JacocoMergeTask

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
            this.isTransitive = false
        }
    }
}

reporting.reports.create<AggregateTestReport>("test") {
    testType.set(TestSuiteType.UNIT_TEST)
}

val jacocoTestReport by reporting.reports.creating(JacocoCoverageReport::class) {
    testType.set(TestSuiteType.UNIT_TEST)
}

val jacocoMergeExecution by tasks.creating(JacocoMergeTask::class) {
    dependsOn(jacocoTestReport.reportTask)
    executionData.from(jacocoTestReport.reportTask.map { it.executionData })
    jacocoClasspath = configurations[JacocoPlugin.ANT_CONFIGURATION_NAME]
}

jacocoTestReport.reportTask {
    // 수동 지정: JacocoReportAggregationPlugin 의 기본값이 위 jacocoAggregation 으로 지정한 의존성들의 출력 중
    // LibraryElements.CLASSES 인 Artifact를 자종 선택하는 방식인데 일부 케이스에서 AmbiguousVariantSelectionException 예외 발생
    // class 경로만 이렇게 처리하고 의존성 관리는 제거하지 않고 나머지 기능에 계속 사용한다.
    val classesDirs = jaCoCoProjectsList.flatMap {
        it.sourceSets.findByName(SourceSet.MAIN_SOURCE_SET_NAME)
            ?.output?.classesDirs ?: emptySet()
    }
    classDirectories.setFrom(classesDirs)

    reports {
        html.required.set(true)
        xml.required.set(true)
    }
}
