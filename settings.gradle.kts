import org.gradle.api.initialization.resolve.RepositoriesMode

pluginManagement {
    // The 'build-infra' and 'build-logic' modules provide plugins for the
    // entire project. Including them here ensures that the plugins are
    // available for resolution in the 'plugins {}' blocks of all build scripts.
    includeBuild("build-infra")
    includeBuild("build-logic")
}

// Configure how dependencies are resolved for the entire project.
@Suppress("UnstableApiUsage")
dependencyResolutionManagement {
    // We opt for a centralized repository definition, where all repositories are
    // declared here. Subprojects will inherit these definitions and should not
    // declare their own, ensuring consistency.
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)

    repositories {
        // Only add 'mavenLocal()' if the 'useMavenLocal' Gradle property is present.
        // This is ideal for local development to speed up builds by using locally
        // cached artifacts without affecting the reproducibility of CI builds.
        if (providers.gradleProperty("useMavenLocal").isPresent) {
            mavenLocal()
        }

        // Define the standard, remote repositories for the project.
        mavenCentral()
        gradlePluginPortal()
    }
}

// Define the root project's name.
rootProject.name = "a2a-jvm"

// Include all subprojects that are part of this build.
include("a2a-jvm-core")
include("a2a-jvm-authentication-bearer")
include("a2a-jvm-authentication-oauth2")
include("a2a-jvm-transport-jsonrpc")
include("a2a-jvm-client")
include("a2a-jvm-server")
include("a2a-jvm-spring-boot-starter")
include("a2a-jvm-examples")
