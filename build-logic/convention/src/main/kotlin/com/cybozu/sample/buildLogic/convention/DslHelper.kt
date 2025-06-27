package com.cybozu.sample.buildLogic.convention

import com.android.build.api.dsl.CommonExtension
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.dsl.KotlinAndroidProjectExtension

inline fun <reified T : CommonExtension<*, *, *, *, *, *>> Project.android(noinline action: T.() -> Unit) {
    val androidExtension = project.extensions.getByName("android") as T
    androidExtension.action()
}

fun Project.kotlinAndroid(action: KotlinAndroidProjectExtension.() -> Unit) = configure(action)

fun Project.java(action: JavaPluginExtension.() -> Unit) = configure(action)

fun <T : Any> Project.propOrDef(propertyName: String, defaultValue: T): T {
    @Suppress("UNCHECKED_CAST")
    val propertyValue = project.properties[propertyName] as T?
    return propertyValue ?: defaultValue
}
