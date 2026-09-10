package com.cybozu.sample.kintone.spaces.data.space.entity

internal data class AddMessageForThreadBody(
    val space: String,
    val thread: String,
    val comment: CommentBody,
)

internal data class CommentBody(
    val text: String,
)
