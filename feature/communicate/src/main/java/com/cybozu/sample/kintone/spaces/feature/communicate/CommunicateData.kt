package com.cybozu.sample.kintone.spaces.feature.communicate

data class ThreadItemData(val id: String, val title: String, val lastMessageSnippet: String)

data class MessageItemData(
    val id: String,
    val threadId: String,
    val userName: String,
    val userIconUrl: String,
    val content: String
)
