plugins {
    `kotlin-dsl`
}

repositories {
    gradlePluginPortal()
}

dependencies {
    implementation(gradleApi())

    implementation(libs.gradlePlugin.kotlin)
}
