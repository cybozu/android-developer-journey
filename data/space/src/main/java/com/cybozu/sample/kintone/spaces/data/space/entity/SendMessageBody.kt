package com.cybozu.sample.kintone.spaces.data.space.entity

internal data class SendMessageBody(
    val threadId: String,
    val message: String,
)
