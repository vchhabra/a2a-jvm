package plugins.conventions

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaLibraryPlugin
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.tasks.testing.Test
import org.gradle.jvm.toolchain.JavaLanguageVersion
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.dsl.KotlinVersion
import org.jetbrains.kotlin.gradle.plugin.KotlinPluginWrapper
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import plugins.extensions.libs

@Suppress("unused")
class JvmCoreLibraryConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.run {
            // Apply essential plugins for a typical JVM library.
            pluginManager.apply(JavaLibraryPlugin::class.java)
            pluginManager.apply(KotlinPluginWrapper::class.java)
            pluginManager.apply(DetektConventionPlugin::class.java)
            pluginManager.apply(CheckstyleConventionPlugin::class.java)
            pluginManager.apply(JacocoConventionPlugin::class.java)

            // Set the project's group and version from Gradle properties.
            // This enforces a consistent project structure.
            val projectGroupId = property("projectGroupId") as String?
                ?: error("The 'projectGroupId' property must be defined in the root gradle.properties file.")
            val projectVersion = property("projectVersion") as String?
                ?: error("The 'projectVersion' property must be defined in the root gradle.properties file.")

            group = projectGroupId
            version = projectVersion

            // Configure the Java toolchain.
            extensions.configure<JavaPluginExtension> {
                // Read the Java version from the version catalog.
                val javaVersion = libs.findVersion("java").get().toString().toInt()
                toolchain {
                    languageVersion.set(JavaLanguageVersion.of(javaVersion))
                }
            }

            // Configure Kotlin compiler options.
            val kotlinVersion = libs.findVersion("kotlin").get().toString()
            val kotlinMajorMinor = kotlinVersion.split('.').take(2).joinToString(".")
            tasks.withType<KotlinCompile>().configureEach {
                compilerOptions {
                    languageVersion.set(KotlinVersion.fromVersion(kotlinMajorMinor))
                    apiVersion.set(KotlinVersion.fromVersion(kotlinMajorMinor))
                }
            }

            // Configure all 'Test' type tasks.
            tasks.withType<Test>().configureEach {
                // Use JUnit Platform for running tests.
                useJUnitPlatform()
            }

            // Add common dependencies.
            dependencies {
                // JUnit 5 for testing.
                add("testImplementation", platform(libs.findLibrary("junit-jupiter-bom").get()))
                add("testImplementation", libs.findLibrary("junit-jupiter-api").get())
                add("testRuntimeOnly", libs.findLibrary("junit-jupiter-engine").get())
                add("testImplementation", libs.findLibrary("junit-platform-launcher").get())
            }
        }
    }
}