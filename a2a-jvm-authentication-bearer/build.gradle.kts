import plugins.conventions.BuildConstants

plugins {
    alias(libs.plugins.jvm.spring.library.convention)
}

dependencies {
    implementation(project(BuildConstants.Modules.A2A_JVM_CORE))
}
