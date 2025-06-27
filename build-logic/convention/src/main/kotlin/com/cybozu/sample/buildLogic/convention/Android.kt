package com.cybozu.sample.buildLogic.convention

import com.android.build.api.dsl.CommonExtension

fun CommonExtension<*, *, *, *, *, *>.configureAndroidCommon() {
    compileSdk = 36

    defaultConfig {
        minSdk = 27
    }

    buildFeatures {
        buildConfig = true
    }
}
