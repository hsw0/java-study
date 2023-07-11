// 여기서는 pluginManagement { repositories { } } 가 적용되지 않음

dependencyResolutionManagement {
    @Suppress("UnstableApiUsage")
    repositories {
        gradlePluginPortal()
    }

    @Suppress("UnstableApiUsage")
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)

    versionCatalogs {
        create("libs") {
            from(files("../libs.versions.toml"))
        }
    }
}
