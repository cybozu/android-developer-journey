package com.cybozu.sample.kintone.spaces.data.space

import com.cybozu.sample.kintone.spaces.data.space.entity.PostMessage
import com.cybozu.sample.kintone.spaces.data.space.entity.Thread
import com.cybozu.sample.kintone.spaces.data.space.entity.ThreadMessage
import javax.inject.Inject

internal class SpaceRepositoryImpl @Inject constructor(
    private val spaceRemoteDataSource: SpaceRemoteDataSource,
) : SpaceRepository {
    override suspend fun getAllThreads(spaceId: String): List<Thread> = spaceRemoteDataSource.getAllThreads(spaceId = spaceId).result.items

    override suspend fun getMessagesForThread(threadId: String): Result<List<ThreadMessage>> =
        spaceRemoteDataSource
            .getMessagesForThread(threadId = threadId)
            .map { it.result.items }

    override suspend fun postMessageForThread(postMessage: PostMessage): Result<Unit> =
        spaceRemoteDataSource.postMessageForThread(postMessage)
}
