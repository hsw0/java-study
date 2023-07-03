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
