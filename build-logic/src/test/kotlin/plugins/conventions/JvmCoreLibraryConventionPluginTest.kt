package plugins.conventions

import org.gradle.testkit.runner.TaskOutcome
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class JvmCoreLibraryConventionPluginTest : ConventionPluginTest() {

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
    fun `applies and configures java, kotlin, jacoco and detekt conventions`() {
        // 1. Arrange
        createSourceFile("src/main/kotlin/com/example/Dummy.kt", DUMMY_KT_SOURCE)
        createSourceFile("src/test/kotlin/com/example/DummyTest.kt", DUMMY_TEST_KT_SOURCE)

        // 2. Act
        val result = runGradle("build", "jacocoTestReport")

        // 3. Assert
        assertEquals(TaskOutcome.SUCCESS, result.task(":compileKotlin")?.outcome)
        assertEquals(TaskOutcome.SUCCESS, result.task(":test")?.outcome)
        assertEquals(TaskOutcome.SUCCESS, result.task(":detekt")?.outcome)
        assertEquals(TaskOutcome.SUCCESS, result.task(":jacocoTestReport")?.outcome)
    }
}
