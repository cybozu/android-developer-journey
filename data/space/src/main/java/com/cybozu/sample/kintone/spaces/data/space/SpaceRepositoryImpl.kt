package com.cybozu.sample.kintone.spaces.data.space

import com.cybozu.sample.kintone.spaces.data.space.entity.Thread
import com.cybozu.sample.kintone.spaces.data.space.entity.ThreadMessage
import javax.inject.Inject
import javax.inject.Singleton

class SpaceRepositoryImpl @Inject constructor(
    private val spaceRemoteDataSource: SpaceRemoteDataSource,
) : SpaceRepository {
    override suspend fun getAllThreads(spaceId: String): List<Thread> = spaceRemoteDataSource.getAllThreads(spaceId = spaceId).result.items

    override suspend fun getMessagesForThread(threadId: String): List<ThreadMessage> =
        spaceRemoteDataSource.getMessagesForThread(threadId = threadId).result.items
}
