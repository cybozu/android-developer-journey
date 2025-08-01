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
        buildConfigField("String", "DOMAIN", "\"${localProperties.getProperty("domain", "")}\"")
    }

    buildFeatures {
        buildConfig = true
    }
}

dependencies {
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)

    implementation(libs.retrofit)
    implementation(libs.retrofit.logging.interceptor)
    implementation(libs.moshi)
    implementation(libs.moshi.converter)
    implementation(libs.moshi.adapters)
    implementation(libs.moshi.kotlin)

    testImplementation(libs.junit)
}
