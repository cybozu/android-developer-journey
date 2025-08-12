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
        val localPropertiesFile = rootProject.file("local.properties")
        if (localPropertiesFile.exists()) {
            localProperties.load(localPropertiesFile.inputStream())
        }
        val user = localProperties.getProperty("user", "dummy_user")
        val password = localProperties.getProperty("password", "dummy_password")
        val domain = localProperties.getProperty("domain", "dummy_domain")
        buildConfigField("String", "USER", "\"$user\"")
        buildConfigField("String", "PASSWORD", "\"$password\"")
        buildConfigField("String", "DOMAIN", "\"$domain\"")
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
