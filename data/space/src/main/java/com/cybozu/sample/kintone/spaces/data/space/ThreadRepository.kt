package com.cybozu.sample.kintone.spaces.data.space

interface ThreadRepository {
    suspend fun postComment(
        spaceId: Int,
        threadId: Int,
        comment: String
    ): Int
}