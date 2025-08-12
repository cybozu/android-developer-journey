package com.cybozu.sample.kintone.spaces.data.space.entity

internal data class ThreadMessageResponse(
    val result: ThreadMessageResult,
    val success: Boolean,
)

internal data class ThreadMessageResult(
    val items: List<ThreadMessage>,
)

data class ThreadMessage(
    val id: String,
    val body: String,
    val creator: Creator,
)

data class Creator(
    val name: String,
)
