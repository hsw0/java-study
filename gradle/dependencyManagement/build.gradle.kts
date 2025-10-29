plugins {
    `java-platform`
}

javaPlatform {
    allowDependencies()
}

dependencies {
    api(enforcedPlatform(libs.bom.kotlin))
    api(enforcedPlatform(libs.bom.kotlinx.coroutines))

    api(platform(libs.bom.springBoot)) {
        exclude(group = "org.jetbrains.kotlin")
        exclude(group = "org.jetbrains.kotlinx")
    }

    api(platform(libs.bom.springCloud.commons))


    for (dependency in libs.bundles.bomDependencies.get()) {
        api(platform(dependency))
    }

    constraints {
        api(libs.bundles.dependencies)
    }
}
