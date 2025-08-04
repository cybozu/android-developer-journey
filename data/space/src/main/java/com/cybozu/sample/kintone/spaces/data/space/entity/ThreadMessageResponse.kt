package com.cybozu.sample.kintone.spaces.data.space.entity

data class ThreadMessageResponse(
    val result: ThreadMessageResult,
    val success: Boolean,
)

data class ThreadMessageResult(
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
