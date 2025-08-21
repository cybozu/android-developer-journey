package com.cybozu.sample.kintone.spaces.data.space.entity

data class PostMessageForThread(
    val space: String,
    val thread: String,
    val comment: PostComment,
    val mentions: List<Mention>,
)

data class PostComment(
    val text: String? = null,
    val files: List<File>? = null,
)

data class Mention(
    val code: String,
    val type: String,
)

data class File(
    val fileKey: String,
//    val width: Width? = null,
)

//sealed interface Width {
//    data class IntValue(
//        val value: Int,
//    ) : Width
//
//    data class StringValue(
//        val value: String,
//    ) : Width
//
//    companion object {
//        operator fun invoke(value: Int): Width = IntValue(value)
//
//        operator fun invoke(value: String): Width = StringValue(value)
//    }
//}
