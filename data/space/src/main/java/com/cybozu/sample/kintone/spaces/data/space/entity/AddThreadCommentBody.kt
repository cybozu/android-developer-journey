package com.cybozu.sample.kintone.spaces.data.space.entity

internal data class AddThreadCommentBody(
    val space: String,
    val thread: String,
    val comment: CommentText,
)

internal data class CommentText(
    val text: String,
)
