package com.cybozu.sample.kintone.spaces.data.space.entity

internal data class CommentForThreadBody(
    val space: Int,
    val thread: Int,
    val comment: BodyComment
)

internal data class BodyComment(
    val text: String,
    val mentions: List<Mention>,
    val files: List<File>
)

internal data class Mention(
    val code: String,
    val type: String
)

internal data class File(
    val fileKey: String,
    val width: Int
)