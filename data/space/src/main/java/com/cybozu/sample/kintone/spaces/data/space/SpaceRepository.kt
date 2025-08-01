package com.cybozu.sample.kintone.spaces.data.space

interface SpaceRepository {
    suspend fun getAllThreads(spaceId: String): List<KintoneThread>

    suspend fun getMessagesForThread(threadId: String): List<KintoneMessage>
}
