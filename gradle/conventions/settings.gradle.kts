dependencyResolutionManagement {
    repositories {
        gradlePluginPortal()
    }

    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)

    versionCatalogs {
        create("libs") {
            from(files("../libs.versions.toml"))
        }
    }
}
