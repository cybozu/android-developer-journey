import java.util.Properties

plugins {
    alias(libs.plugins.convention.android.library)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.cybozu.sample.kintone.spaces.data.space"

    defaultConfig {
        val localProperties = Properties()
        localProperties.load(rootProject.file("local.properties").inputStream())
        buildConfigField("String", "USER", "\"${localProperties.getProperty("user", "")}\"")
        buildConfigField("String", "PASSWORD", "\"${localProperties.getProperty("password", "")}\"")
    }

    buildFeatures {
        buildConfig = true
    }
}

dependencies {
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)

    implementation(libs.retrofit)

    testImplementation(libs.junit)
}
