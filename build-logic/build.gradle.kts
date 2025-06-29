import org.gradle.api.tasks.testing.Test
import org.gradle.kotlin.dsl.implementation
import org.gradle.kotlin.dsl.named
import org.gradle.kotlin.dsl.withType
import org.gradle.plugin.devel.GradlePluginDevelopmentExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import plugins.infrastructure.BuildConstantsGeneratorExtension

plugins {
    `kotlin-dsl`
    // Apply the plugin by its ID. This is the correct approach.
    id("build-constants-generator")
}

gradlePlugin {
    plugins {
        // A map of plugin IDs to their implementation classes.
        val conventionPlugins = mapOf(
            "detekt-convention" to "plugins.conventions.DetektConventionPlugin",
            "jacoco-convention" to "plugins.conventions.JacocoConventionPlugin",
            "jvm-core-library-convention" to "plugins.conventions.JvmCoreLibraryConventionPlugin",
            "jvm-spring-library-convention" to "plugins.conventions.JvmSpringLibraryConventionPlugin",
            "spring-boot-app-convention" to "plugins.conventions.SpringBootAppConventionPlugin"
        )

        // Iterate over the map to register each plugin. This reduces boilerplate.
        conventionPlugins.forEach { (pluginId, implementationClass) ->
            register(pluginId) {
                id = pluginId
                this.implementationClass = implementationClass
            }
        }
    }
}

// Disable caching for the 'compileKotlin' task in 'build-logic'.
// Gradle Doctor has identified that this task is very fast to execute,
// making it quicker to rerun than to retrieve from the build cache.
tasks.named<KotlinCompile>("compileKotlin") {
    outputs.cacheIf { false }
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()

    // Pass the content of the root libs.versions.toml file as a system property
    // to the tests. This makes it available to the ConventionPluginTest class.
    val tomlContent = project.gradle.parent!!.rootProject.file("gradle/libs.versions.toml").readText()
    systemProperty("libs.versions.toml.content", tomlContent)

    // Read the root detekt.yml file and pass its content as a system property.
    val detektConfigContent = project.gradle.parent!!.rootProject.file("tools/detekt/detekt.yml").readText()
    systemProperty("detekt.config.content", detektConfigContent)

    // Pass the absolute path of the build-logic directory as a system property.
    // This is required for the test's settings.gradle.kts to correctly include the build.
    systemProperty("buildLogic.dir", project.projectDir.absolutePath)
}

configure<BuildConstantsGeneratorExtension> {
    packageName.set("plugins.conventions")

    val conventionPlugins = project.extensions.getByType<GradlePluginDevelopmentExtension>()
    conventionPluginIds.set(conventionPlugins.plugins.map { it.id })

    val moduleNamesList = project.gradle.parent?.rootProject?.subprojects?.map { it.path } ?: emptyList()
    moduleNames.set(moduleNamesList)
}

dependencies {
    // Add the full Gradle API to the classpath.
    implementation(gradleApi())

    // Add Gradle plugins as dependencies to use their APIs in our convention plugins.
    implementation(libs.kotlin.gradle.plugin)
    implementation(libs.kotlin.allopen.plugin)
    implementation(libs.spring.boot.gradle.plugin)
    implementation(libs.detekt.gradle.plugin)

    // Add testing dependencies.
    testImplementation(platform(libs.junit.jupiter.bom))
    testImplementation("org.junit.jupiter:junit-jupiter-api")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
    testImplementation("org.junit.platform:junit-platform-launcher")
}
