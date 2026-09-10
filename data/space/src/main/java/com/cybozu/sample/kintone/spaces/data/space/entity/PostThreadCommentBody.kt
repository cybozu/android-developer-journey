package com.cybozu.sample.kintone.spaces.data.space.entity

internal data class PostThreadCommentBody(
    val space: String,
    val thread: String,
    val comment: PostThreadCommentContent,
)

internal data class PostThreadCommentContent(
    val text: String,
)
