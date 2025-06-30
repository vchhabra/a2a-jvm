package plugins.conventions

import org.gradle.testkit.runner.TaskOutcome
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.io.File

/**
 * Tests for the [CheckstyleConventionPlugin]. These tests verify that the plugin
 * correctly applies Checkstyle, uses the project's configuration, and fails the
 * build when violations are found.
 */
class CheckstyleConventionPluginTest : ConventionPluginTest() {

    // This property will be lazy-initialized from the system property we pass in.
    private val expectedCheckstyleVersion: String by lazy {
        System.getProperty("checkstyle.version")
            ?: error("The 'checkstyle.version' system property was not found.")
    }

    // A compliant Java class with proper packaging and Javadoc.
    private val compliantJavaSource = """
        package com.example;

        /**
         * A compliant Java class used for testing.
         */
        public class MyClass {}
    """.trimIndent() + "\n"

    // A compliant Java test class with a proper structure.
    private val compliantJavaTestSource = """
        package com.example;

        import org.junit.jupiter.api.Test;

        /**
         * A compliant Java test class.
         */
        public class MyClassTest {
            /**
             * A placeholder test method.
             */
            @Test
            public void myTest() {
                // Test logic goes here.
            }
        }
    """.trimIndent() + "\n"

    // A non-compliant Java class missing a package declaration.
    private val nonCompliantJavaSource = """
        /**
         * This class is non-compliant because it lacks a package declaration.
         */
        public class BadClass {
        }
    """.trimIndent() + "\n"

    // A non-compliant Java class that violates whitespace rules.
    private val invalidJavaSource = """
        package com.example;

        class BadClass{void method(){}} // Missing spaces around braces
    """.trimIndent() + "\n"

    @BeforeEach
    fun setup() {
        val buildScript = """
            plugins {
                // The 'jvm-core-library-convention' applies the Java, Checkstyle,
                // and other necessary plugins, setting a realistic test environment.
                id("jvm-core-library-convention")
            }
            
            // Custom task to inspect the Checkstyle extension configuration
            tasks.register("inspectCheckstyleConfig") {
                doLast {
                    val checkstyleExt = project.extensions.getByType(org.gradle.api.plugins.quality.CheckstyleExtension::class.java)
                    println("toolVersion:" + checkstyleExt.toolVersion)
                    println("configDirectory:" + checkstyleExt.configDirectory.get().asFile.name)
                }
            }
        """
        setupTestProject(buildKts = buildScript)
    }

    @Test
    fun `checkstyle plugin is applied`() {
        // Act: Run the 'tasks' command to see if checkstyle tasks are available.
        val result = runGradle("tasks", "--all")

        // Assert: Check that the output includes the description for Checkstyle tasks.
        assertTrue(result.output.contains("checkstyleMain - "), "The checkstyleMain task should be registered.")
        assertTrue(result.output.contains("checkstyleTest - "), "The checkstyleTest task should be registered.")
    }

    @Test
    fun `checkstyle extension is configured correctly`() {
        // Arrange
        val expectedConfigDir = "checkstyle"

        // Act
        val result = runGradle("inspectCheckstyleConfig")

        // Assert - Now uses the property instead of a hardcoded string
        assertTrue(result.output.contains("toolVersion:$expectedCheckstyleVersion"), "Checkstyle toolVersion should be configured correctly.")
        assertTrue(result.output.contains("configDirectory:$expectedConfigDir"), "Checkstyle configDirectory should be configured correctly.")
    }

    @Test
    fun `missing suppression file should fail the build`() {
        // Arrange: Create a compliant source file, but delete the suppression file
        // which our main checkstyle.xml requires.
        createSourceFile("src/main/java/com/example/MyClass.java", compliantJavaSource)
        val suppressionFile = File(projectDir, "tools/checkstyle/checkstyle-suppressions.xml")
        assertTrue(suppressionFile.exists(), "Precondition: Suppression file should exist before deletion.")
        suppressionFile.delete()

        // Act: Run the 'check' task and expect it to fail.
        val result = runGradleAndFail("check")

        // Assert: Verify that the task failed for the correct reason.
        assertEquals(TaskOutcome.FAILED, result.task(":checkstyleMain")?.outcome)
        assertTrue(
            result.output.contains("cannot initialize module SuppressionFilter"),
            "Build should fail due to the missing suppression file."
        )

        // Create a regex to find the error message regardless of the absolute path.
        // It looks for "Unable to find:" followed by any characters, ending with our filename.
        val expectedErrorPattern = "Unable to find:.*checkstyle-suppressions\\.xml".toRegex()
        assertTrue(
            expectedErrorPattern.containsMatchIn(result.output),
            "Build output should contain the error message for the missing suppression file."
        )
    }

    @Test
    fun `checkstyle tasks succeed for compliant code`() {
        // Arrange
        createSourceFile("src/main/java/com/example/MyClass.java", compliantJavaSource)
        createSourceFile("src/test/java/com/example/MyClassTest.java", compliantJavaTestSource)

        // Act: Run the 'check' task, which depends on all checkstyle tasks.
        val result = runGradle("check")

        // Assert: Verify that the checkstyle tasks completed successfully.
        assertEquals(TaskOutcome.SUCCESS, result.task(":checkstyleMain")?.outcome)
        assertEquals(TaskOutcome.SUCCESS, result.task(":checkstyleTest")?.outcome)
    }

    @Test
    fun `checkstyle task fails for non-compliant code`() {
        // Arrange: Create a source file that violates Checkstyle rules.
        createSourceFile("src/main/java/BadClass.java", nonCompliantJavaSource)

        // Act: Run the 'check' task and expect it to fail.
        val result = runGradleAndFail("check")

        // Assert: Verify that the main checkstyle task failed as expected.
        assertEquals(TaskOutcome.FAILED, result.task(":checkstyleMain")?.outcome)

        // And assert that the output clearly indicates the cause of the failure.
        val output = result.output
        assertTrue(output.contains("Checkstyle rule violations were found"), "Build output: $output")
        assertTrue(output.contains("Missing package declaration"), "Build output should mention the missing package.")
    }

    @Test
    fun `checkstyle rules are enforced for whitespace`() {
        // Arrange
        createSourceFile("src/main/java/com/example/BadClass.java", invalidJavaSource)

        // Act
        val result = runGradleAndFail("check")

        // Assert
        assertEquals(TaskOutcome.FAILED, result.task(":checkstyleMain")?.outcome)
        assertTrue(result.output.contains("Checkstyle rule violations were found"), "Build output: ${result.output}")
        assertTrue(result.output.contains("WhitespaceAround: '{' is not preceded with whitespace"), "Build output should mention the specific whitespace violation.")
    }
}
