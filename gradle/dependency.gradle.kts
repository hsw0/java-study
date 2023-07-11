plugins {
    `java-platform`
}

javaPlatform {
    allowDependencies()
}

dependencies {
    api(enforcedPlatform(libs.bom.kotlin))
    api(enforcedPlatform(libs.bom.kotlinx.coroutines))

    for (dependency in libs.bundles.bomDependencies.get()) {
        api(platform(dependency))
    }

    constraints {
        api(libs.bundles.dependencies)
    }
}
