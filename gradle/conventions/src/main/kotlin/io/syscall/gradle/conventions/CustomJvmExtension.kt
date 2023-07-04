@file:Suppress("RedundantVisibilityModifier")

package io.syscall.gradle.conventions

import org.gradle.api.provider.Property
import org.gradle.jvm.toolchain.JavaLanguageVersion

public interface CustomJvmExtension {
    val javaToolchain: Property<JavaLanguageVersion>
    val javaTarget: Property<JavaLanguageVersion>
    val kotlinToolchain: Property<JavaLanguageVersion>
    val kotlinJvmTarget: Property<JavaLanguageVersion>
    val runtimeJdkVersion: Property<JavaLanguageVersion>
}

internal abstract class DefaultCustomJvmExtension : CustomJvmExtension {
    init {
        javaToolchain.convention(JavaLanguageVersion.of(21))
        javaTarget.convention(JavaLanguageVersion.of(17))
        kotlinToolchain.convention(JavaLanguageVersion.of(21))
        kotlinJvmTarget.convention(JavaLanguageVersion.of(17))


        runtimeJdkVersion.convention(JavaLanguageVersion.of(21)) // TODO
    }
}

/**
 * mrjar 플러그인 사용시 전역으로 버전을 지정하지 않도록 함
 *
 * @see me.champeau.mrjar.MultiReleaseJarPlugin
 */
public object UnconfiguredVersion : JavaLanguageVersion {
    override fun compareTo(other: JavaLanguageVersion?): Int {
        if (other is UnconfiguredVersion) {
            return 0
        }
        return 1
    }

    override fun asInt(): Int = 0

    override fun canCompileOrRun(other: JavaLanguageVersion): Boolean {
        TODO("Not yet implemented")
    }

    override fun canCompileOrRun(otherVersion: Int): Boolean {
        TODO("Not yet implemented")
    }

}
