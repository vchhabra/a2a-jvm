package plugins.conventions

import org.gradle.testkit.runner.TaskOutcome
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import java.io.File
import org.junit.jupiter.api.Test

class JacocoConventionPluginTest : ConventionPluginTest() {

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
    fun `jacocoTestReport generates xml and html reports`() {
        // 1. Arrange
        createSourceFile("src/main/kotlin/com/example/Dummy.kt", DUMMY_KT_SOURCE)
        createSourceFile("src/test/kotlin/com/example/DummyTest.kt", DUMMY_TEST_KT_SOURCE)

        // 2. Act: Run the 'test' and 'jacocoTestReport' tasks.
        val result = runGradle("test", "jacocoTestReport")

        // 3. Assert: Verify that both tasks succeeded and generated the expected reports.
        assertEquals(TaskOutcome.SUCCESS, result.task(":test")?.outcome)
        assertEquals(TaskOutcome.SUCCESS, result.task(":jacocoTestReport")?.outcome)

        val htmlReport = File(projectDir, "build/reports/jacoco/test/html/index.html")
        val xmlReport = File(projectDir, "build/reports/jacoco/test/jacocoTestReport.xml")
        assertTrue(htmlReport.exists(), "Expected HTML report was not generated. ${listFilesIn("build/reports")}")
        assertTrue(xmlReport.exists(), "Expected XML report was not generated. ${listFilesIn("build/reports")}")
    }
}
