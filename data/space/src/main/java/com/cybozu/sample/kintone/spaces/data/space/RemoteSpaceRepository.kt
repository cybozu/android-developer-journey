package com.cybozu.sample.kintone.spaces.data.space

import com.cybozu.sample.kintone.spaces.data.space.entity.Thread
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RemoteSpaceRepository @Inject constructor(
    private val spaceRemoteDataSource: SpaceRemoteDataSource
) : SpaceRepository {

    override suspend fun getAllThreads(spaceId: String): List<Thread> {
        return spaceRemoteDataSource.getAllThreads(spaceId = spaceId).result.items
    }

    override suspend fun getMessagesForThread(threadId: String): List<KintoneMessage> {
        return spaceRemoteDataSource.getMessagesForThread(threadId = threadId)
    }
}

