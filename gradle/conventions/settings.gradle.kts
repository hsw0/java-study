rootProject.name = "project-conventions"

dependencyResolutionManagement {
    // 참고: Version catalog를 선언하더라도 build.gradle.kts가 아닌 플러그인 내에서는 typesafe accessor을 사용할 수 없다
    // Make version catalogs accessible from precompiled script pluginshttps://github.com/gradle/gradle/issues/15383
    versionCatalogs {
        create("libs") {
            from(files("../libs.versions.toml"))
        }
    }
}
