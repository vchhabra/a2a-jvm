package plugins.conventions // Assuming your package name

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.quality.Checkstyle
import org.gradle.api.plugins.quality.CheckstyleExtension
import org.gradle.api.plugins.quality.CheckstylePlugin
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.withType
import plugins.extensions.libs

/**
 * Convention plugin to apply Checkstyle to Java source files.
 * This plugin configures the Checkstyle extension and sets up the source
 * directories for analysis, using versions from the project's version catalog.
 */
@Suppress("UnstableApiUsage")
internal class CheckstyleConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply(CheckstylePlugin::class.java)

            extensions.configure<CheckstyleExtension> {
                toolVersion = libs.findVersion("checkstyle").get().toString()

                // Set the configuration directory. This is the correct way to handle
                // co-located configuration files like suppressions.xml.
                configDirectory.set(rootProject.file("tools/checkstyle"))

                // Set the maximum number of errors to 0 to fail the build on any violation.
                maxErrors = 0
            }

            tasks.withType<Checkstyle>().configureEach {
                // By default, Checkstyle will fail the build on violations because maxErrors is 0.
                // We no longer need to set ignoreFailures = false.
                reports {
                    xml.required.set(true)
                    html.required.set(true)
                    sarif.required.set(true)
                }
            }
        }
    }
}
