package com.cybozu.sample.kintone.spaces.data.space

data class KintoneMessage(
    val id: String,
    val threadId: String,
    val userName: String,
    val userIconUrl: String,
    val content: String,
)
