package com.cybozu.sample.kintone.spaces.feature.communicate

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
data object SpaceRoute : NavKey

@Serializable
data class ThreadRoute(
    val threadId: String,
    val threadName: String,
) : NavKey
