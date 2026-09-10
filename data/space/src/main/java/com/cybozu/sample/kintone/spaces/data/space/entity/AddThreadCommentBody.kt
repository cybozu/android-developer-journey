package com.cybozu.sample.kintone.spaces.data.space.entity

internal data class AddThreadCommentBody(
    val space: String,
    val thread: String,
    val comment: AddThreadCommentContent,
)

internal data class AddThreadCommentContent(
    val text: String,
)
