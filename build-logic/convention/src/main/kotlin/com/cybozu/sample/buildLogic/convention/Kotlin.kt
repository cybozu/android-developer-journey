package com.cybozu.sample.buildLogic.convention

import org.gradle.api.Project
import org.gradle.jvm.toolchain.JavaLanguageVersion
import org.gradle.kotlin.dsl.assign
import org.jetbrains.kotlin.gradle.dsl.HasConfigurableKotlinCompilerOptions
import org.jetbrains.kotlin.gradle.dsl.KotlinProjectExtension

fun <T> Project.configureKotlinCommon(kotlinExtension: T) where T : KotlinProjectExtension, T : HasConfigurableKotlinCompilerOptions<*> {
    kotlinExtension.apply {
        compilerOptions {
            allWarningsAsErrors = propOrDef(propertyName = "warningsAsErrors", defaultValue = "false").toBoolean()
        }
    }

    java {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(21))
        }
    }
}
