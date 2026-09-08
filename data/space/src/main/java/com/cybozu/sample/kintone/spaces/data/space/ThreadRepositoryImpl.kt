package com.cybozu.sample.kintone.spaces.data.space

import com.cybozu.sample.kintone.spaces.data.space.entity.PostThreadCommentBody
import javax.inject.Inject

internal class ThreadRepositoryImpl @Inject constructor(
    private val remoteDataSource: ThreadRemoteDataSource
): ThreadRepository {
    override suspend fun postComment(
        spaceId: Int,
        threadId: Int,
        comment: String
    ): Int {
        return remoteDataSource.postComment(
            spaceId,
            threadId,
            comment
        ).id
    }
}