package io.syscall.gradle.plugin.jacoco

import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.FileCollection
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.internal.project.IsolatedAntBuilder
import org.gradle.api.tasks.IgnoreEmptyDirectories
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.SkipWhenEmpty
import org.gradle.api.tasks.TaskAction
import org.gradle.kotlin.dsl.withGroovyBuilder
import org.gradle.testing.jacoco.tasks.JacocoBase
import java.io.File

/**
 * JaCoCo Execution 데이터들을 병합
 *
 * Gradle 7.1 에서 제거됨. [Deprecate JacocoMerge task #12767](https://github.com/gradle/gradle/issues/12767), [Release note](https://docs.gradle.org/7.1/release-notes.html)
 * </p>
 */
@Suppress("RedundantVisibilityModifier")
public abstract class JacocoMergeTask : JacocoBase() {

    @get:OutputFile
    public val outputLocation: RegularFileProperty = project.objects.fileProperty()

    @get:InputFiles
    @get:IgnoreEmptyDirectories
    @get:SkipWhenEmpty
    @get:PathSensitive(PathSensitivity.RELATIVE)
    public val executionData: ConfigurableFileCollection = project.objects.fileCollection()

    private val taskName get() = this.name

    init {
        outputLocation.convention(project.layout.buildDirectory.file("jacoco/$taskName.exec"))

        @Suppress("LeakingThis")
        onlyIf("Any of the execution data files exists") {
            executionData.any(File::exists)
        }
    }

    @TaskAction
    fun merge() {
        val outputLocation = this.outputLocation.get()

        val antBuilder = services[IsolatedAntBuilder::class.java]
        antBuilder.withClasspath(jacocoClasspath).execute {
            taskdef(
                mapOf(
                    "name" to taskName,
                    "classname" to "org.jacoco.ant.MergeTask",
                ),
            )

            withGroovyBuilder {
                taskName("destfile" to outputLocation) {
                    executionData.addToAntBuilder(this, "fileset", FileCollection.AntType.FileSet)
                }
            }
        }
    }
}
