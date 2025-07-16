package com.cybozu.sample.buildLogic.convention

import org.gradle.api.Project
import org.gradle.kotlin.dsl.assign
import org.gradle.kotlin.dsl.configure
import org.jlleitschuh.gradle.ktlint.KtlintExtension

fun Project.configureLint() {
    pluginManager.apply(libs.getPluginId("ktlint-gradle"))

    ktlint {
        android = true
    }
}

private fun Project.ktlint(action: KtlintExtension.() -> Unit) = configure(action)

