package com.cybozu.sample.kintone.spaces.feature.communicate

import kotlinx.serialization.Serializable

@Serializable
object SpaceRoute

@Serializable
data class ThreadRoute(
    val threadId: String,
    val threadName: String,
)
