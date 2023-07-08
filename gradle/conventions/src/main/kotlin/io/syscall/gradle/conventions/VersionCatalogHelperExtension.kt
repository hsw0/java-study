package io.syscall.gradle.conventions

import org.gradle.api.Project
import org.gradle.api.artifacts.MinimalExternalModuleDependency
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.api.artifacts.VersionConstraint
import org.gradle.api.provider.Provider
import org.gradle.kotlin.dsl.getByType

/**
 * 참고: Version catalog를 선언하더라도 build.gradle.kts가 아닌 플러그인 내에서는 typesafe accessor을 사용할 수 없다
 *
 * [Make version catalogs accessible from precompiled script plugins](https://github.com/gradle/gradle/issues/15383)
 */
private object Comments

internal val Project.versionCatalog: VersionCatalog
    get() = extensions.getByType<VersionCatalogsExtension>().named("libs")

internal val VersionCatalog.libs
    get() = IndexedAccessOperator<MinimalExternalModuleDependency> { alias ->
        this.findLibrary(alias)
            .map(Provider<MinimalExternalModuleDependency>::get)
            .orElseThrow { NoSuchElementException("'$alias': Not found in Version Catalog libraries section") }
    }

internal val VersionCatalog.versions
    get() = IndexedAccessOperator<VersionConstraint> { alias ->
        this.findVersion(alias)
            .orElseThrow { NoSuchElementException("'$alias': Not found in Version Catalog libraries section") }
    }

internal fun interface IndexedAccessOperator<T> {
    /**
     * @throws NoSuchElementException When undefined in version catalog (libs.version.toml)
     */
    operator fun get(alias: String): T
}
