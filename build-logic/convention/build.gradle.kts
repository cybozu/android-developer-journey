plugins {
    `kotlin-dsl`
}

dependencies {
    compileOnly(libs.android.gradle.plugin)
    compileOnly(libs.kotlin.gradle.plugin)
    compileOnly(libs.ktlint.gradle.plugin)
}

gradlePlugin {
    plugins {
        register("androidApplication") {
            id = libs.plugins.convention.android.application.get().pluginId
            implementationClass = "AndroidApplicationPlugin"
        }
        register("androidLibrary") {
            id = libs.plugins.convention.android.library.get().pluginId
            implementationClass = "AndroidLibraryPlugin"
        }
    }
}
