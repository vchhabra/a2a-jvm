package plugins.conventions

import org.gradle.testkit.runner.TaskOutcome
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.io.File

class DetektConventionPluginTest : ConventionPluginTest() {

    @BeforeEach
    fun setup() {
        val buildScript = """
            plugins {
                id("jvm-core-library-convention")
            }
        """
        // Define the build script that applies the convention plugin by its ID.
        setupTestProject(buildKts = buildScript)
    }

    @Test
    fun `detekt fails for file without final newline`() {
        // 1. Arrange
        // This source file violates the 'FinalNewline' rule.
        createSourceFile("src/main/kotlin/com/example/Bad.kt", "class Bad")

        // 2. Act
        val result = runGradleAndFail("detekt")

        // 3. Assert
        assertEquals(TaskOutcome.FAILED, result.task(":detekt")?.outcome)
        assertTrue(result.output.contains("File must end with a newline"), "Build output should mention the FinalNewline violation.")
    }

    @Test
    fun `detekt with autoCorrect adds a final newline`() {
        // 1. Arrange
        val badSourcePath = "src/main/kotlin/com/example/Bad.kt"
        createSourceFile(badSourcePath, "class Bad")

        // 2. Act
        val result = runGradleAndFail("detekt", "-Pdetekt.autoCorrect=true")

        // 3. Assert
        assertEquals(TaskOutcome.FAILED, result.task(":detekt")?.outcome, "Detekt should fail if violations are found, even with autoCorrect.")
        val correctedContent = File(projectDir, badSourcePath).readText()
        assertEquals("class Bad\n", correctedContent, "The source file should have been auto-corrected.")
    }
}
