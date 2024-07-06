package io.syscall.gradle.conventions

import org.gradle.api.artifacts.Configuration
import org.gradle.api.internal.tasks.JvmConstants.ANNOTATION_PROCESSOR_CONFIGURATION_NAME
import org.gradle.api.internal.tasks.JvmConstants.COMPILE_CLASSPATH_CONFIGURATION_NAME
import org.gradle.api.internal.tasks.JvmConstants.RUNTIME_CLASSPATH_CONFIGURATION_NAME
import java.util.Locale

// 전체 Configuration 목록들은 $ ./gradlew ${project.path}:resolvableConfigurations 로 확인 가능

internal val Configuration.isClasspathLike: Boolean
    get() = isCompileClasspath || isAnnotationProcessorClasspath || isRuntimeClasspath
        || name == "kapt"

// 참고: main 소스셋의 경우 그냥 "compileClasspath", 그 외 소스셋은 ${sourceSet.name}CompileClasspath
// ex) testCompileClasspath, productionRuntimeClasspath (spring boot plugin)
internal val String.nameIsCompileClasspath: Boolean
    get() = this == COMPILE_CLASSPATH_CONFIGURATION_NAME
        || this.endsWith(COMPILE_CLASSPATH_CONFIGURATION_NAME.capitalized())

internal val Configuration.isCompileClasspath: Boolean
    get() = name.nameIsCompileClasspath

internal val String.nameIsAnnotationProcessorClasspath: Boolean
    get() = this == ANNOTATION_PROCESSOR_CONFIGURATION_NAME
        || this.endsWith(ANNOTATION_PROCESSOR_CONFIGURATION_NAME.capitalized())

internal val Configuration.isAnnotationProcessorClasspath: Boolean
    get() = name.nameIsAnnotationProcessorClasspath

// 소스셋별 규칙은 상동

internal val String.nameIsRuntimeClasspath: Boolean
    get() = this == RUNTIME_CLASSPATH_CONFIGURATION_NAME
        || this.endsWith(RUNTIME_CLASSPATH_CONFIGURATION_NAME.capitalized())

internal val Configuration.isRuntimeClasspath: Boolean
    get() = name.nameIsRuntimeClasspath

internal fun CharSequence.capitalized(): String = when {
    isEmpty() -> ""
    else -> this[0].let { initial ->
        when {
            initial.isLowerCase() -> initial.titlecase(Locale.getDefault()) + substring(1)
            else -> toString()
        }
    }
}
