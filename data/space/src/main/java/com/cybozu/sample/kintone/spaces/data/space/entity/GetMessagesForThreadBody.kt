package com.cybozu.sample.kintone.spaces.data.space.entity

data class GetMessagesForThreadBody(
    val size: Int = 20,
    val threadId: String,
)
