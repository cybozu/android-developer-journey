package com.cybozu.sample.kintone.spaces.data.space.entity

internal data class PostThreadCommentBody(
    val space: String,
    val thread: String,
    val comment: CommentText,
)

internal data class CommentText(
    val text: String,
)
