package com.cybozu.sample.kintone.spaces.data.space.entity

data class PostMessage(
    val space: String,
    val thread: String,
    val comment: PostComment,
)

data class PostComment(
    val text: String? = null,
)
