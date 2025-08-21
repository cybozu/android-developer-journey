package com.cybozu.sample.kintone.spaces.data.space.entity

// SpaceServiceで使用しているためpublicで宣言

data class CommentMessageForThreadBody(
    val space: String,
    val thread: String,
    val comment: BodyComment
)

data class BodyComment(
    val text: String,
    val mentions: List<Mention>?,
    val files: List<File>?
)

data class Mention(
    val code: String,
    val type: String
)

data class File(
    val fileKey: String,
    val width: Int
)