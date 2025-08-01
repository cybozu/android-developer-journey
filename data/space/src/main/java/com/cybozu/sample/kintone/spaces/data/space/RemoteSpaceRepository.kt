package com.cybozu.sample.kintone.spaces.data.space

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RemoteSpaceRepository @Inject constructor(
    private val spaceRemoteDataSource: SpaceRemoteDataSource
) : SpaceRepository {

    override suspend fun getAllThreads(spaceId: String): List<KintoneThread> {
        return spaceRemoteDataSource.getAllThreads(spaceId = spaceId)
    }

    override suspend fun getMessagesForThread(threadId: String): List<KintoneMessage> {
        return spaceRemoteDataSource.getMessagesForThread(threadId = threadId)
    }
}

