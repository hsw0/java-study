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
