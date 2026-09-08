package com.cybozu.sample.kintone.spaces.data.space.entity

data class PostThreadCommentBody(
    val space: Int,
    val thread: Int,
    val comment: Comment
) {
    data class Comment(
        val text: String
    )
}