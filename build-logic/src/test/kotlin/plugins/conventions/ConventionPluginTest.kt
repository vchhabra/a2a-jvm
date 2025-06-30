package plugins.conventions

import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.GradleRunner
import org.junit.jupiter.api.io.TempDir
import java.io.File

/**
 * An abstract base class for testing convention plugins. It provides a shared,
 * temporary project directory and helper methods for setting up and running
 * Gradle builds. This class also defines fake source code constants that are
 * compliant with the project's Detekt rules.
 */
abstract class ConventionPluginTest {

    @TempDir
    lateinit var projectDir: File

    // The name assigned to the root project in the test's settings.gradle.kts.
    protected val testProjectName: String = "test-project"

    // Lazy-initialized properties to read file contents only when needed.
    // Making this protected allows subclasses to access project properties directly.
    protected val projectProperties: String by lazy {
        // The 'buildLogic.dir' property is passed from the build-logic/build.gradle.kts
        // to the test execution. We use it to locate the root project's properties file.
        val buildLogicDir = System.getProperty("buildLogic.dir")
            ?: error("The 'buildLogic.dir' system property is required.")
        File(buildLogicDir).parentFile.resolve("gradle.properties").readText()
    }

    private val projectTOML: String by lazy {
        System.getProperty("libs.versions.toml.content")
            ?: error("The 'libs.versions.toml.content' system property was not found.")
    }

    private val detektConfig: String by lazy {
        System.getProperty("detekt.config.content")
            ?: error("The 'detekt.config.content' system property was not found.")
    }

    /**
     * Helper property to get the Checkstyle config directory path from system properties.
     */
    private val checkstyleConfigDir: String by lazy {
        System.getProperty("checkstyle.config.dir")
            ?: error("The 'checkstyle.config.dir' system property was not found.")
    }

    companion object {
        /**
         * A valid, multi-line Kotlin source file that complies with Detekt rules.
         * The trailing newline is significant and required by the `FinalNewline` rule.
         */
        val DUMMY_KT_SOURCE = """
            package com.example

            class Dummy
        """.trimIndent() + "\n"

        /**
         * A valid test source file that complies with all Detekt rules, including
         * the `EmptyFunctionBlock` rule.
         */
        val DUMMY_TEST_KT_SOURCE = """
            package com.example

            import org.junit.jupiter.api.Test

            class DummyTest {
                @Test
                fun `a placeholder test`() {
                    // This is a placeholder test to ensure the test execution works correctly.
                }
            }
        """.trimIndent() + "\n"
    }

    /**
     * Sets up the test project with the specified build script and project files.
     * This method creates the essential Gradle configuration files, including
     * `settings.gradle.kts`, `build.gradle.kts`, and the version catalog.
     */
    protected fun setupTestProject(buildKts: String) {
        val buildLogicDir = System.getProperty("buildLogic.dir")
        File(projectDir, "settings.gradle.kts").writeText("""
            pluginManagement {
                includeBuild("$buildLogicDir")
            }
            // The test project needs to know where to resolve dependencies from.
            // We define the standard repositories here.
            dependencyResolutionManagement {
                repositories {
                    mavenCentral()
                    gradlePluginPortal()
                }
            }
            rootProject.name = "$testProjectName"
        """.trimIndent())

        // Create the build script with the provided content.
        File(projectDir, "build.gradle.kts").writeText(buildKts)

        // Create the required gradle.properties file.
        File(projectDir, "gradle.properties").writeText(projectProperties)

        // Create the version catalog file.
        val gradleDir = File(projectDir, "gradle")
        gradleDir.mkdirs()
        File(gradleDir, "libs.versions.toml").writeText(projectTOML)

        // Create the Detekt configuration from the project's default.
        createDefaultDetektConfig()

        // Copy over checkstyle configurations from the project's default.
        copyCheckstyleConfig()
    }

    /**
     * Creates a `tools/detekt/detekt.yml` file in the test project using the
     * content of the production configuration, which is passed in via a
     * system property during the build.
     */
    private fun createDefaultDetektConfig() {
        val configFile = File(projectDir, "tools/detekt/detekt.yml")
        configFile.parentFile.mkdirs()
        configFile.writeText(detektConfig)
    }

    /**
     * Copies the entire Checkstyle configuration directory from the main project
     * into the temporary test project directory.
     */
    private fun copyCheckstyleConfig() {
        val sourceDir = File(checkstyleConfigDir)
        if (sourceDir.exists()) {
            // The destination is tools/checkstyle within the temporary projectDir
            val destDir = File(projectDir, "tools/checkstyle")
            sourceDir.copyRecursively(destDir, overwrite = true)
        } else {
            // This case should not happen if the system property is set correctly.
            error("Source checkstyle config directory not found at: $checkstyleConfigDir")
        }
    }

    /**
     * Creates a source file at the specified path within the test project.
     * The parent directories will be created if they do not exist.
     */
    protected fun createSourceFile(path: String, content: String) {
        val file = File(projectDir, path)
        file.parentFile.mkdirs()
        file.writeText(content)
    }

    /**
     * Executes a Gradle build with the given arguments and expects it to succeed.
     */
    protected fun runGradle(vararg args: String): BuildResult {
        return GradleRunner.create()
            .withProjectDir(projectDir)
            .withArguments(args.toList() + "--stacktrace")
            .build()
    }

    /**
     * Executes a Gradle build with the given arguments and expects it to fail.
     */
    protected fun runGradleAndFail(vararg args: String): BuildResult {
        return GradleRunner.create()
            .withProjectDir(projectDir)
            .withArguments(args.toList() + "--stacktrace")
            .buildAndFail()
    }

    /**
     * Helper function to list files in a directory for debugging purposes.
     */
    protected fun listFilesIn(dir: String): String {
        val subDir = File(projectDir, dir)
        return if (subDir.exists() && subDir.isDirectory) {
            subDir.walkTopDown().joinToString("\n") { it.relativeTo(projectDir).path }
        } else {
            "Directory '$dir' does not exist."
        }
    }
}
