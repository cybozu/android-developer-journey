package com.cybozu.sample.kintone.spaces.data.space.entity

data class ThreadListResponse(
    val result: ThreadResult,
    val success: Boolean,
)

data class ThreadResult(
    val items: List<Thread>,
)

data class Thread(
    val id: String,
    val spaceId: String,
    val name: String,
    val body: String?,
)
