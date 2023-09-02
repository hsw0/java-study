@file:Suppress("RedundantVisibilityModifier")

package io.syscall.gradle.conventions

import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.jvm.toolchain.JavaToolchainService
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.support.serviceOf
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension

public val Project.sourceSets: SourceSetContainer get() = extensions.getByType()

public val Project.javaToolchainService: JavaToolchainService get() = serviceOf()

public val Project.java: JavaPluginExtension get() = extensions.getByType()

public val Project.kotlin: KotlinJvmProjectExtension get() = extensions.getByType()
