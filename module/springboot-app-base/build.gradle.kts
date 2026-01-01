plugins {
    id("conventions.project.java")
    id("conventions.project.java-analysis")
    id("conventions.project.spring-boot")
    `java-library`
    `java-test-fixtures`
}

group = "dummy"

//region Source Sets & Capabilities

val variants = listOf("webflux", "servlet")

// Create variant source sets
val variantSourceSets = variants.associateWith { name ->
    sourceSets.create(name) {
        compileClasspath += sourceSets.main.get().output
        runtimeClasspath += sourceSets.main.get().output
    }
}

// Create test source sets for each variant
val testSourceSets = variants.associateWith { name ->
    sourceSets.create("test${name.replaceFirstChar { it.uppercase() }}") {
        compileClasspath +=
            sourceSets.main.get().output + variantSourceSets[name]!!.output + sourceSets.testFixtures.get().output
        runtimeClasspath +=
            sourceSets.main.get().output + variantSourceSets[name]!!.output + sourceSets.testFixtures.get().output
    }
}

// Configure variant configurations to extend from main
configurations {
    for (variant in variants) {
        named("${variant}Implementation") { extendsFrom(implementation.get()) }
        named("${variant}CompileOnly") { extendsFrom(compileOnly.get()) }
        named("${variant}RuntimeOnly") { extendsFrom(runtimeOnly.get()) }
        named("${variant}AnnotationProcessor") { extendsFrom(annotationProcessor.get()) }
    }

    // Test configurations extend from variant + testFixtures
    for (variant in variants) {
        val testName = "test${variant.replaceFirstChar { it.uppercase() }}"
        named("${testName}Implementation") {
            extendsFrom(named("${variant}Implementation").get())
            extendsFrom(testFixturesApi.get())
        }
        named("${testName}CompileOnly") { extendsFrom(named("${variant}CompileOnly").get()) }
        named("${testName}RuntimeOnly") { extendsFrom(named("${variant}RuntimeOnly").get()) }
        named("${testName}AnnotationProcessor") { extendsFrom(named("${variant}AnnotationProcessor").get()) }
    }
}

// Register capabilities for variant selection
java {
    for ((name, sourceSet) in variantSourceSets) {
        registerFeature(name) {
            usingSourceSet(sourceSet)
            capability(project.group.toString(), "springboot-app-base-impl", version.toString())
            capability(project.group.toString(), "springboot-app-base-$name", version.toString())
        }
    }
}

//endregion
//region Dependencies

dependencies {
    // Common dependencies
    implementation(project(":module:logging-support"))
    implementation(project(":module:springboot-support"))
    implementation(project(":module:reactor-support"))
    implementation(project(":module:protocol"))

    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.springframework.boot:spring-boot-starter-aspectj")
    implementation("org.springframework.boot:spring-boot-validation")
    implementation("org.springframework.boot:spring-boot-jackson")
    implementation("org.springframework.boot:spring-boot-web-server")
    implementation("org.springframework.boot:spring-boot-reactor")
    implementation("org.springframework.boot:spring-boot-actuator-autoconfigure")
    implementation("org.springframework.boot:spring-boot-micrometer-metrics")
    implementation("org.springframework.cloud:spring-cloud-context") {
        exclude(group = "org.springframework.security", module = "spring-security-crypto")
    }

    runtimeOnly("io.micrometer:context-propagation")
    runtimeOnly("io.opentelemetry:opentelemetry-context")
    runtimeOnly("org.springframework.boot:spring-boot-jackson")
    runtimeOnly("org.springframework.boot:spring-boot-health")
    runtimeOnly("tools.jackson.module:jackson-module-kotlin")

    val webfluxImplementation by configurations
    val servletImplementation by configurations

    // WebFlux variant
    webfluxImplementation("org.springframework.boot:spring-boot-webflux")
    webfluxImplementation("org.springframework.boot:spring-boot-reactor-netty")

    // Servlet variant
    servletImplementation("jakarta.servlet:jakarta.servlet-api")
    servletImplementation("org.springframework.boot:spring-boot-webmvc")
    servletImplementation("org.springframework:spring-web")

    // Test fixtures
    testFixturesApi("org.springframework.boot:spring-boot-starter-test")
    testFixturesApi("org.springframework.boot:spring-boot-webtestclient")
    testFixturesAnnotationProcessor("org.springframework:spring-context-indexer")

    // Test dependencies for each variant
    for (variant in variants) {
        val testConfig = "test${variant.replaceFirstChar { it.uppercase() }}"
        "${testConfig}RuntimeOnly"("org.junit.platform:junit-platform-launcher")
    }
    "testServletImplementation"("org.springframework.boot:spring-boot-starter-web")
}

//endregion
//region Test Tasks

for ((variant, sourceSet) in testSourceSets) {
    val taskName = "test${variant.replaceFirstChar { it.uppercase() }}"
    tasks.register<Test>(taskName) {
        description = "Runs tests for ${variant.replaceFirstChar { it.uppercase() }} variant"
        group = "verification"
        testClassesDirs = sourceSet.output.classesDirs
        classpath = sourceSet.runtimeClasspath
        useJUnitPlatform()
    }
}

tasks.named<Test>("test") {
    dependsOn(testSourceSets.keys.map { "test${it.replaceFirstChar { c -> c.uppercase() }}" })
    enabled = false
}

//endregion
