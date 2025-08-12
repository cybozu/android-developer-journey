package com.cybozu.sample.kintone.spaces.data.space.entity

internal data class ThreadListResponse(
    val result: ThreadResult,
    val success: Boolean,
)

internal data class ThreadResult(
    val items: List<Thread>,
)

data class Thread(
    val id: String,
    val spaceId: String,
    val name: String,
    val body: String?,
)
