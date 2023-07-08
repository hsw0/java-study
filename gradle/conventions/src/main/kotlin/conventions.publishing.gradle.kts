import org.gradle.internal.component.external.model.TestFixturesSupport

/**
 * Maven publishing
 *
 * @see org.gradle.api.publish.maven.plugins.MavenPublishPlugin
 */
private object Comments

plugins {
    `maven-publish`
}

plugins.withType<JavaLibraryPlugin> {
    configure<JavaPluginExtension> {
        withJavadocJar()
        withSourcesJar()
    }

    val sourceSets = extensions.getByType<SourceSetContainer>()

    with(components["java"] as AdhocComponentWithVariants) {
        // Example 25. Disable publishing of test fixtures variants
        // https://docs.gradle.org/8.2/userguide/java_testing.html#ex-disable-publishing-of-test-fixtures-variants
        plugins.withType<JavaTestFixturesPlugin> {
            sourceSets.findByName(TestFixturesSupport.TEST_FIXTURES_FEATURE_NAME)?.apply {
                withVariantsFromConfiguration(configurations[apiElementsConfigurationName]) { skip() }
                withVariantsFromConfiguration(configurations[runtimeElementsConfigurationName]) { skip() }
            }
        }
    }

    val publishing = extensions.getByType<PublishingExtension>()

    publishing.publications.create<MavenPublication>("artifact") {
        groupId = project.group.toString()
        artifactId = project.properties["publishing.artifactId"] as String? ?: project.name

        withBuildIdentifier()
        from(components["java"])
    }
    // TODO: repositories
}
