plugins {
    `java-platform`
}

javaPlatform {
    allowDependencies()
}

dependencies {
    api(enforcedPlatform(libs.bom.kotlin))
    api(enforcedPlatform(libs.bom.junit))
    api(platform(libs.bom.springBoot))

    constraints {
        api("org.slf4j:slf4j-api:2.0.7")
    }
}
