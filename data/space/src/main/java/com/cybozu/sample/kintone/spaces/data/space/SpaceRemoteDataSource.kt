package com.cybozu.sample.kintone.spaces.data.space

import javax.inject.Inject

class SpaceRemoteDataSource @Inject constructor(
    private val spaceService: SpaceService,
) {
    suspend fun getAllThreads(spaceId: String): List<KintoneThread> {
        return spaceService.getAllThreads(
            encodeString = "",
            body = GetAllThreadsBody(spaceId = spaceId)
        )
    }

    suspend fun getMessagesForThread(threadId: String): List<KintoneMessage> {
        return spaceService.getMessagesForThread(
            encodeString = "",
            body = GetMessagesForThreadBody(threadId = threadId)
        )
    }
}